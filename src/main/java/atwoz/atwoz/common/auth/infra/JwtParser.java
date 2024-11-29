package atwoz.atwoz.common.auth.infra;

import atwoz.atwoz.common.auth.domain.Role;
import atwoz.atwoz.common.auth.infra.exception.JwtException;
import io.jsonwebtoken.Claims;
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
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .setAllowedClockSkewSeconds(ALLOWED_CLOCK_SKEW_SECONDS)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (io.jsonwebtoken.JwtException e) {
            throw new JwtException(e.getMessage());
        }
    }
}
