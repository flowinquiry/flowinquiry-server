package io.flexwork;

import static io.flexwork.db.DbConstants.MASTER_SCHEMA;

import io.flexwork.config.ApplicationProperties;
import io.flexwork.config.FlexworkProfiles;
import io.flexwork.config.FlexworkProperties;
import io.flexwork.db.TenantContext;
import io.flexwork.db.service.LiquibaseService;
import io.flexwork.modules.usermanagement.domain.Tenant;
import io.flexwork.modules.usermanagement.service.TenantService;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.config.DefaultProfileUtil;

@SpringBootApplication
@EnableConfigurationProperties({
    LiquibaseProperties.class,
    ApplicationProperties.class,
    FlexworkProperties.class
})
@EntityScan("io.flexwork")
@EnableAspectJAutoProxy
@Order(1)
public class FlexworkApp {

    private static final Logger LOG = LoggerFactory.getLogger(FlexworkApp.class);

    private final TenantService tenantService;

    private final LiquibaseService liquibaseService;

    private final Environment env;

    public FlexworkApp(
            Environment env, LiquibaseService liquibaseService, TenantService tenantService) {
        this.env = env;
        this.tenantService = tenantService;
        this.liquibaseService = liquibaseService;
    }

    /**
     * Initializes flexwork.
     *
     * <p>Spring profiles can be configured with a program argument
     * --spring.profiles.active=your-active-profile
     */
    @PostConstruct
    public void initApplication() {
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains(FlexworkProfiles.SPRING_PROFILE_DEVELOPMENT)
                && activeProfiles.contains(FlexworkProfiles.SPRING_PROFILE_PRODUCTION)) {
            LOG.error(
                    "You have misconfigured your application! It should not run "
                            + "with both the 'dev' and 'prod' profiles at the same time.");
        }
        migrateDatabases(activeProfiles);
    }

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        loadEnvVariablesFromEnvFile(".");
        loadEnvVariablesFromEnvFile("..");

        SpringApplication app = new SpringApplication(FlexworkApp.class);

        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
    }

    private static void loadEnvVariablesFromEnvFile(String path) {
        Dotenv dotEnv =
                Dotenv.configure()
                        .directory(path)
                        .filename(".env.local")
                        .systemProperties()
                        .ignoreIfMissing()
                        .load();
        if (!dotEnv.entries().isEmpty()) {
            LOG.info("Loaded env variables from {}", path + "/.env.local");
        }
    }

    private static void logApplicationStartup(Environment env) {
        String protocol =
                Optional.ofNullable(env.getProperty("server.ssl.key-store"))
                        .map(key -> "https")
                        .orElse("http");
        String applicationName = env.getProperty("spring.application.name");
        String serverPort = env.getProperty("server.port");
        String contextPath =
                Optional.ofNullable(env.getProperty("server.servlet.context-path"))
                        .filter(StringUtils::isNotBlank)
                        .orElse("/");
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            LOG.warn("The host name could not be determined, using `localhost` as fallback");
        }
        LOG.info("----------------------------------------------------------");
        LOG.info("\tApplication '{}' is running! Access URLs:", applicationName);
        LOG.info("\tLocal: \t\t{}://localhost:{}{}", protocol, serverPort, contextPath);
        LOG.info("\tExternal: \t{}://{}:{}{}", protocol, hostAddress, serverPort, contextPath);
        LOG.info(
                "\tProfile(s): \t{}",
                env.getActiveProfiles().length == 0
                        ? env.getDefaultProfiles()
                        : env.getActiveProfiles());
    }

    @Transactional
    void migrateDatabases(Collection<String> activeProfiles) {
        liquibaseService.updateMasterDbSchema(MASTER_SCHEMA, activeProfiles);
        Tenant defaultTenant = tenantService.getDefaultTenant();
        LOG.debug("Default tenant: {}", defaultTenant);
        liquibaseService.createTenantDbSchema(defaultTenant.getName(), activeProfiles);
        TenantContext.setCurrentTenant(defaultTenant.getName());
    }
}
