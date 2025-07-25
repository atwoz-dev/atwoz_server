package atwoz.atwoz.common.config;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Slf4j
@Configuration
@Profile("!test")
public class FlywayConfig {
    @Bean
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
            .baselineOnMigrate(true)
            .dataSource(dataSource)
            .load();
    }

    @Bean
    public ApplicationRunner migrateFlyway(Flyway flyway) {
        return args -> {
            try {
                flyway.migrate();
            } catch (Exception e) {
                log.error("Flyway migration failed", e);
            }
        };
    }
}