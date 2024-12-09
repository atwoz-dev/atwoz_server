package atwoz.atwoz.common.auth.jwt;

import atwoz.atwoz.common.auth.context.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtProvider {

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

    public String createAccessToken(long id, Role role, Instant now) {
        return createToken(id, role, accessTokenExpiration, now);
    }

    public String createRefreshToken(long id, Role role, Instant now) {
        return createToken(id, role, refreshTokenExpiration, now);
    }

    private String createToken(long id, Role role, long expiration, Instant now) {
        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .claim(ROLE, role)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(expiration)))
                .signWith(key)
                .compact();
    }
}
