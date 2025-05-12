package atwoz.atwoz.payment.command.infra.order;

import com.apple.itunes.storekit.client.AppStoreServerAPIClient;
import com.apple.itunes.storekit.migration.ReceiptUtility;
import com.apple.itunes.storekit.model.Environment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;

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
        byte[] decodedBytes = Base64.getDecoder().decode(privateKeyString);
        String decodedPrivateKeyString = new String(decodedBytes);
        return new AppStoreServerAPIClient(decodedPrivateKeyString, keyId, issuerId, bundleId, environment);
    }

    @Bean
    public ReceiptUtility receiptUtility() {
        return new ReceiptUtility();
    }
}