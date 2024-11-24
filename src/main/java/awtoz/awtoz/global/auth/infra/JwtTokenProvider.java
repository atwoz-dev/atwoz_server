package awtoz.awtoz.global.auth.infra;

import awtoz.awtoz.global.auth.domain.TokenProvider;
import awtoz.awtoz.global.auth.infra.exception.*;
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
public class JwtTokenProvider implements TokenProvider {


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

    @Override
    public String createAccessToken(Long id) {
        Claims claims = Jwts.claims();
        claims.put("id", id);
        claims.put("token_type", "access_token");
        claims.put("role", "member");

        return createToken(claims, accessTokenExpirationTime);
    }

    @Override
    public String createRefreshToken(Long id) {
        return "";
    }

    @Override
    public <T> T extract(String token, String claimName, Class<T> classType) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secret.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get(claimName, classType);
        } catch (SecurityException e) {
            throw new SignatureInvalidException();
        } catch (MalformedJwtException e) {
            throw new TokenFormInvalidException();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedTokenException();
        } catch (IllegalArgumentException e) {
            throw new TokenInvalidException();
        }
    }

    private String createToken(Claims claims, int expirationTime) {
        return Jwts.builder()
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
