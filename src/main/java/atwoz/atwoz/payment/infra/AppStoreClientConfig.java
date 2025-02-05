package atwoz.atwoz.payment.infra;

import com.apple.itunes.storekit.client.AppStoreServerAPIClient;
import com.apple.itunes.storekit.model.Environment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppStoreClientConfig {

    @Bean
    public AppStoreServerAPIClient appStoreServerAPIClient(
            @Value("${payment.app-store.key-id}") String keyId,
            @Value("${payment.app-store.issuer-id}") String issuerId,
            @Value("${payment.app-store.private-key-string}") String privateKeyString,
            @Value("${payment.app-store.bundle-id}") String bundleId,
            @Value("${payment.app-store.environment}") String environmentValue) {
        Environment environment = Environment.fromValue(environmentValue);
        return new AppStoreServerAPIClient(privateKeyString, keyId, issuerId, bundleId, environment);
    }
}