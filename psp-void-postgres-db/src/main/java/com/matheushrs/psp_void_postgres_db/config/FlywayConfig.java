package com.matheushrs.psp_void_postgres_db.config;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.boot.jdbc.init.DataSourceScriptDatabaseInitializer;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Wires Flyway into Spring Boot 4's database-initialization lifecycle.
 *
 * Spring Boot 4 removed the Flyway auto-configuration from its core module,
 * so we register a bean that extends DataSourceScriptDatabaseInitializer.
 * This ensures DataSourceScriptDatabaseInitializerDetector picks it up as a
 * "database initializer", and JpaDependsOnDatabaseInitializationDetector
 * then makes the JPA EntityManagerFactory depend on it — guaranteeing that
 * Flyway migrations run before Hibernate validates the schema.
 */
@Configuration
public class FlywayConfig {

    @Bean
    public FlywayMigrationInitializer flywayMigrationInitializer(DataSource dataSource) {
        return new FlywayMigrationInitializer(dataSource);
    }

    @Slf4j
    static class FlywayMigrationInitializer extends DataSourceScriptDatabaseInitializer {

        private final DataSource ds;

        FlywayMigrationInitializer(DataSource dataSource) {
            super(dataSource, new DatabaseInitializationSettings());
            this.ds = dataSource;
        }

        @Override
        public boolean initializeDatabase() {
            var result = Flyway.configure()
                    .dataSource(ds)
                    .locations("classpath:db/migration")
                    .baselineOnMigrate(true)
                    .baselineVersion("1")
                    .load()
                    .migrate();
            if (result.migrationsExecuted > 0) {
                log.info("Flyway applied {} migration(s) successfully.", result.migrationsExecuted);
            } else {
                log.info("Flyway: schema is up to date (no migrations applied).");
            }
            return result.migrationsExecuted > 0;
        }
    }
}
