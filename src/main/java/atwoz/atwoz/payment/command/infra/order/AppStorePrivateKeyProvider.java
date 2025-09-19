package atwoz.atwoz.payment.command.infra.order;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Component
public class AppStorePrivateKeyProvider {

    @Value("${payment.app-store.private-key-string}")
    private String privateKeyString;

    @Getter
    private PrivateKey privateKey;

    @PostConstruct
    public void init() {
        try {
            this.privateKey = parsePrivateKey();
            log.info("App Store Private Key 초기화 완료");
        } catch (Exception e) {
            throw new IllegalStateException("App Store Private Key 초기화 실패", e);
        }
    }

    private PrivateKey parsePrivateKey() throws Exception {
        byte[] decodedBytes = Base64.getDecoder().decode(privateKeyString);
        String decodedPrivateKeyString = new String(decodedBytes);

        String privateKeyPEM = decodedPrivateKeyString
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s+", "");

        byte[] keyBytes = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePrivate(spec);
    }
}