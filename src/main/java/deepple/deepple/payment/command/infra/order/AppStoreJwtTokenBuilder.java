package deepple.deepple.payment.command.infra.order;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class AppStoreJwtTokenBuilder {

    private static final long TOKEN_VALIDITY_SECONDS = 3600; // 1시간

    private final AppStorePrivateKeyProvider privateKeyProvider;

    @Value("${payment.app-store.key-id}")
    private String keyId;

    @Value("${payment.app-store.issuer-id}")
    private String issuerId;

    @Value("${payment.app-store.bundle-id}")
    private String bundleId;

    public String buildToken() {
        PrivateKey privateKey = privateKeyProvider.getPrivateKey();
        Instant currentTime = Instant.now();

        return Jwts.builder()
            .setHeaderParam("kid", keyId)
            .setHeaderParam("typ", "JWT")
            .setIssuer(issuerId)
            .setIssuedAt(Date.from(currentTime))
            .setExpiration(createExpirationDate(currentTime))
            .setAudience("appstoreconnect-v1")
            .claim("bid", bundleId)
            .signWith(privateKey, SignatureAlgorithm.ES256)
            .compact();
    }

    private Date createExpirationDate(Instant currentTime) {
        return Date.from(currentTime.plusSeconds(TOKEN_VALIDITY_SECONDS));
    }
}
