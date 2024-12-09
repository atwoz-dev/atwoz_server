package atwoz.atwoz.common.auth.jwt;

import atwoz.atwoz.common.auth.context.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtParser {

    private static final String ROLE = "role";
    private final Key key;

    public JwtParser(@Value("${jwt.secret}") String secret) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public boolean isValid(String token) {
        try {
            parseJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Long getIdFrom(String token) {
        return Long.parseLong(getSubject(token));
    }

    public Role getRoleFrom(String token) {
        return Role.valueOf(getClaim(token, ROLE, String.class));
    }

    public Instant getExpirationFrom(String token) {
        return getExpiration(token)
                .toInstant()
                .truncatedTo(ChronoUnit.SECONDS);
    }

    private Jws<Claims> parseJws(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    private Claims parseClaims(String token) {
        return parseJws(token).getBody();
    }

    private String getSubject(String token) {
        return parseClaims(token).getSubject();
    }

    private Date getExpiration(String token) {
        return parseClaims(token).getExpiration();
    }

    private <T> T getClaim(String token, String claimName, Class<T> requiredType) {
        return parseClaims(token).get(claimName, requiredType);
    }
}
