package atwoz.atwoz.payment.command.infra.order;

import com.apple.itunes.storekit.client.AppStoreServerAPIClient;
import com.apple.itunes.storekit.migration.ReceiptUtility;
import com.apple.itunes.storekit.model.Environment;
import com.apple.itunes.storekit.verification.SignedDataVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Bean
    public SignedDataVerifier signedDataVerifier(
        @Value("${payment.app-store.root-ca-paths}") String[] rootCaPaths,
        @Value("${payment.app-store.bundle-id}") String bundleId,
        @Value("${payment.app-store.app-apple-id}") Long appAppleId,
        @Value("${payment.app-store.environment}") String environmentValue) {
        Environment environment = Environment.fromValue(environmentValue);
        Set<InputStream> rootCAs = Arrays.stream(rootCaPaths)
            .map(path -> {
                try {
                    return new FileInputStream(path);
                } catch (FileNotFoundException e) {
                    throw new UncheckedIOException(e);
                }
            })
            .collect(Collectors.toSet());
        if (environment.equals(Environment.SANDBOX)) {
            appAppleId = null;
        }
        return new SignedDataVerifier(rootCAs, bundleId, appAppleId, environment, true);
    }
}