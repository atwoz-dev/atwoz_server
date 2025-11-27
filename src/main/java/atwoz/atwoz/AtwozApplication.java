package atwoz.atwoz;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
@EnableJpaAuditing
@EnableFeignClients
public class AtwozApplication {
    public static void main(String[] args) {
        SpringApplication.run(AtwozApplication.class, args);
    }

    @PostConstruct
    public void started() {
        // set timezone
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
}
