package io.flexwork.db.service;

import java.sql.Connection;
import java.util.Collection;
import javax.sql.DataSource;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LiquibaseService {

    private static final Logger LOG = LoggerFactory.getLogger(LiquibaseService.class);

    private static final String MASTER_CHANGESET = "config/liquibase/master/master.xml";

    private static final String TENANT_CHANGESET = "config/liquibase/tenant/master.xml";

    private DataSource dataSource;

    public LiquibaseService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @SneakyThrows
    private void updateLiquibaseSchema(
            String classpathChangeset, String schema, Collection<String> activeProfiles) {
        try (Connection connection = dataSource.getConnection()) {
            LOG.info("Going to create a schema {}", schema);
            connection.prepareCall("CREATE SCHEMA IF NOT EXISTS " + schema).execute();
            // Create the database for the default tenant
            Database database =
                    DatabaseFactory.getInstance()
                            .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            database.setDefaultSchemaName(schema);
            Liquibase liquibase =
                    new Liquibase(classpathChangeset, new ClassLoaderResourceAccessor(), database);
            Contexts contexts = new Contexts();
            activeProfiles.forEach(contexts::add);
            liquibase.update(contexts, new LabelExpression());
            liquibase.close();
        }
    }

    @Transactional
    public void createTenantDbSchema(String schema, Collection<String> activeProfiles) {
        updateLiquibaseSchema(TENANT_CHANGESET, schema, activeProfiles);
    }

    @Transactional
    public void updateMasterDbSchema(String schema, Collection<String> activeProfiles) {
        updateLiquibaseSchema(MASTER_CHANGESET, schema, activeProfiles);
    }
}
