package io.flexwork.db.service;

import jakarta.transaction.Transactional;
import java.sql.Connection;
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
    private void updateLiquibaseSchema(String classpathChangeset, String schema) {
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
            liquibase.update(new Contexts(), new LabelExpression());
            liquibase.close();
        }
    }

    @Transactional
    public void createTenantDbSchema(String schema) {
        updateLiquibaseSchema(TENANT_CHANGESET, schema);
    }

    @Transactional
    public void updateMasterDbSchema(String schema) {
        updateLiquibaseSchema(MASTER_CHANGESET, schema);
    }
}
