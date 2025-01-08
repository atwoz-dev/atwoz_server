package atwoz.atwoz.auth.infra;

import atwoz.atwoz.auth.domain.Role;
import atwoz.atwoz.auth.domain.TokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtProvider implements TokenProvider {

    private static final String ROLE = "role";

    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final Key key;

    public JwtProvider(@Value("${jwt.secret}") String secret,
                       @Value("${jwt.access-token.expiration}") int accessTokenExpiration,
                       @Value("${jwt.refresh-token.expiration}") int refreshTokenExpiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String createAccessToken(long id, Role role, Instant issuedAt) {
        return createToken(id, role, accessTokenExpiration, issuedAt);
    }

    public String createRefreshToken(long id, Role role, Instant issuedAt) {
        return createToken(id, role, refreshTokenExpiration, issuedAt);
    }

    private String createToken(long id, Role role, long expiration, Instant issuedAt) {
        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .claim(ROLE, role)
                .setIssuedAt(Date.from(issuedAt))
                .setExpiration(Date.from(issuedAt.plusSeconds(expiration)))
                .signWith(key)
                .compact();
    }
}
