package atwoz.atwoz.common.auth.jwt;

import atwoz.atwoz.common.auth.context.Role;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class JwtIntegrationTest {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    private JwtProvider jwtProvider;

    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        jwtProvider = new JwtProvider(secret, accessTokenExpiration, refreshTokenExpiration);
        jwtParser = new JwtParser(secret);
    }

    @Test
    @DisplayName("Access token을 생성합니다.")
    void createAccessToken() {
        // given
        long id = 1L;
        Role role = Role.MEMBER;
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);

        // when
        String token = jwtProvider.createAccessToken(id, role, now);

        // then
        assertThat(token).isNotNull();
        assertThat(jwtParser.getIdFrom(token)).isEqualTo(id);
        assertThat(jwtParser.getRoleFrom(token)).isEqualTo(role);
        Instant expirationFrom = jwtParser.getExpirationFrom(token);
        assertThat(expirationFrom)
                .isEqualTo(now.plus(Duration.ofSeconds(accessTokenExpiration)));
    }

    @Test
    @DisplayName("Refresh token을 생성합니다.")
    void createRefreshToken() {
        // given
        long id = 1L;
        Role role = Role.MEMBER;
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);

        // when
        String token = jwtProvider.createRefreshToken(id, role, now);

        // then
        assertThat(token).isNotNull();
        assertThat(jwtParser.getIdFrom(token)).isEqualTo(id);
        assertThat(jwtParser.getRoleFrom(token)).isEqualTo(role);
        assertThat(jwtParser.getExpirationFrom(token))
                .isEqualTo(now.plus(Duration.ofSeconds(refreshTokenExpiration)));
    }

    @Test
    @DisplayName("올바르지 않은 형식의 토큰에서는 id를 파싱할 수 없습니다.")
    void cannotParseIdFromInvalidToken() {
        // given
        String token = "invalid token";

        // when & then
        assertThatThrownBy(() -> jwtParser.getIdFrom(token)).isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("올바르지 않은 형식의 토큰에서는 Role을 파싱할 수 없습니다.")
    void cannotParseRoleFromInvalidToken() {
        // given
        String token = "invalid token";

        // when & then
        assertThat(jwtParser.isValid(token)).isFalse();
        assertThatThrownBy(() -> jwtParser.getRoleFrom(token)).isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("올바르지 않은 형식의 토큰에서는 expiration을 파싱할 수 없습니다.")
    void cannotParseExpirationFromInvalidToken() {
        // given
        String token = "invalid token";

        // when & then
        assertThat(jwtParser.isValid(token)).isFalse();
        assertThatThrownBy(() -> jwtParser.getExpirationFrom(token)).isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("만료된 access token은 파싱할 수 없습니다.")
    void cannotParseExpiredAccessToken() {
        // given
        long id = 1L;
        Role role = Role.MEMBER;
        Instant before = Instant.now().minusSeconds(accessTokenExpiration + 60).truncatedTo(ChronoUnit.SECONDS);

        // when
        String token = jwtProvider.createAccessToken(id, role, before);

        // then
        assertThat(jwtParser.isExpired(token)).isTrue();
        assertThatThrownBy(() -> jwtParser.getExpirationFrom(token)).isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @DisplayName("만료된 refresh token은 파싱할 수 없습니다.")
    void cannotParseExpiredRefreshToken() {
        // given
        long id = 1L;
        Role role = Role.MEMBER;
        Instant before = Instant.now().minusSeconds(refreshTokenExpiration + 60).truncatedTo(ChronoUnit.SECONDS);

        // when
        String token = jwtProvider.createRefreshToken(id, role, before);

        // then
        assertThat(jwtParser.isExpired(token)).isTrue();
        assertThatThrownBy(() -> jwtParser.getExpirationFrom(token)).isInstanceOf(ExpiredJwtException.class);
    }
}
