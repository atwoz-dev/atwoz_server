package awtoz.awtoz.common.auth.infra;

import awtoz.awtoz.common.auth.domain.Role;
import awtoz.awtoz.common.auth.infra.exception.*;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@NoArgsConstructor
@Component
public class JwtProvider {

    private static final String ROLE = "role";

    // TODO : 환경변수 설정 필요.
    @Value("${jwt.secret}")
    private String secret;

    // TODO : 토큰 별 시간 정의 필요.
    @Value("${jwt.access_token.expiration}")
    private int accessTokenExpirationTime;

    @Value("${jwt.refresh_token.expiration}")
    private int refreshTokenExpirationTime;

    private Key key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public JwtProvider(String secret, int accessTokenExpirationTime, int refreshTokenExpirationTime) {
        this.secret = secret;
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String createAccessToken(Long id, Role role) {
        Claims claims = Jwts.claims();
        claims.setSubject(id.toString());
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
        return Role.valueOf(parseClaims(token).get(ROLE, String.class));
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
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationTime * 1000L))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
