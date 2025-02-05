package atwoz.atwoz.payment.infra;

import atwoz.atwoz.payment.infra.exception.PrivateKeyFileException;
import com.apple.itunes.storekit.client.AppStoreServerAPIClient;
import com.apple.itunes.storekit.model.Environment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class AppStoreClientConfig {

    @Bean
    public AppStoreServerAPIClient appStoreServerAPIClient(
            @Value("${payment.app-store.key-id}") String keyId,
            @Value("${payment.app-store.issuer-id}") String issuerId,
            @Value("${payment.app-store.private-key-file-path}") String privateKeyFilePath,
            @Value("${payment.app-store.bundle-id}") String bundleId,
            @Value("${payment.app-store.environment}") String environmentValue) {
        Environment environment = Environment.fromValue(environmentValue);
        String encodedKey = readPrivateKey(privateKeyFilePath);
        return new AppStoreServerAPIClient(encodedKey, keyId, issuerId, bundleId, environment);
    }

    private String readPrivateKey(String filePath) {
        try {
            return Files.readString(Path.of(filePath));
        } catch (Exception e) {
            throw new PrivateKeyFileException(e);
        }
    }
}