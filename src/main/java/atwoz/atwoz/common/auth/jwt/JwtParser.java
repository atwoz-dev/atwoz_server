package atwoz.atwoz.common.auth.jwt;

import atwoz.atwoz.common.auth.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class JwtParser {

    private static final String ROLE = "role";
    public static final int ALLOWED_CLOCK_SKEW_SECONDS = 60;

    private final Key key;

    public JwtParser(@Value("${jwt.secret}") String secret) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public boolean isInvalid(String token) {
        return !isValid(token);
    }

    public Long getIdFrom(String token) {
        return Long.parseLong(parseClaims(token).getSubject());
    }

    public Role getRoleFrom(String token) {
        return Role.valueOf(parseClaims(token).get(ROLE, String.class));
    }

    public Instant getExpirationFrom(String token) {
        return parseClaims(token).getExpiration().toInstant().truncatedTo(ChronoUnit.SECONDS);
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .setAllowedClockSkewSeconds(ALLOWED_CLOCK_SKEW_SECONDS)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .setAllowedClockSkewSeconds(ALLOWED_CLOCK_SKEW_SECONDS)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
