package awtoz.awtoz.common.auth.infra;

import awtoz.awtoz.common.auth.domain.Role;
import awtoz.awtoz.common.auth.infra.exception.*;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@NoArgsConstructor
@Component
public class JwtTokenProvider {

    private static final String ROLE = "role";

    // TODO : 환경변수 설정 필요.
    private String secret = "this-is-secret-key-value-at-least-128-bytes";

    // TODO : 토큰 별 시간 정의 필요.
    private int accessTokenExpirationTime = 60 * 60 * 24;
//    private int refreshTokenExpirationTime = 60 * 60 * 24;

    private Key key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String createAccessToken(Long id, Role role) {
        Claims claims = Jwts.claims();
        claims.put(ROLE, role);

        return createToken(claims, id, accessTokenExpirationTime);
    }

    public String createRefreshToken(Long id) {
        return "";
    }

    public Long extractId(String token) {
        return Long.parseLong(parseClaims(token).getSubject());
    }

    public Role extractRole(String token) {
        return parseClaims(token).get(ROLE, Role.class);
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secret.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        } catch (SecurityException e) {
            throw new SignatureInvalidException();
        } catch (MalformedJwtException e) {
            throw new TokenFormInvalidException();
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedTokenException();
        } catch (IllegalArgumentException e) {
            throw new TokenInvalidException();
        }
    }

    private String createToken(Claims claims, Long id, int expirationTime) {
        return Jwts.builder()
                .setSubject(id.toString())
                .setClaims(claims)
                .setIssuedAt(issuedAt())
                .setExpiration(expiredAt(expirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Date issuedAt() {
        LocalDateTime now = LocalDateTime.now();

        return Date.from(now.atZone(ZoneId.systemDefault())
                .toInstant());
    }

    private Date expiredAt(int expirationTime) {
        LocalDateTime now = LocalDateTime.now();

        return Date.from(now.plusDays(expirationTime)
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }
}
