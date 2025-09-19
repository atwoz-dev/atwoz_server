package atwoz.atwoz.payment.command.infra.order;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AppStoreApiConfig {

    @Value("${payment.app-store.environment:Sandbox}")
    private String environment;

    public String getBaseUrl() {
        return isProductionEnvironment()
            ? "https://api.storekit.itunes.apple.com"
            : "https://api.storekit-sandbox.itunes.apple.com";
    }

    private boolean isProductionEnvironment() {
        return "Production".equalsIgnoreCase(environment);
    }
}