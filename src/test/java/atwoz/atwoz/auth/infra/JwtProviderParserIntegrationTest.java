package atwoz.atwoz.auth.infra;

import atwoz.atwoz.common.enums.Role;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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
class JwtProviderParserIntegrationTest {

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private JwtParser jwtParser;

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
        assertThat(jwtParser.getId(token)).isEqualTo(id);
        assertThat(jwtParser.getRole(token)).isEqualTo(role);
        Instant expirationFrom = jwtParser.getExpiration(token);
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
        assertThat(jwtParser.getId(token)).isEqualTo(id);
        assertThat(jwtParser.getRole(token)).isEqualTo(role);
        assertThat(jwtParser.getExpiration(token))
            .isEqualTo(now.plus(Duration.ofSeconds(refreshTokenExpiration)));
    }

    @Test
    @DisplayName("올바르지 않은 형식의 토큰에서는 id를 파싱할 수 없습니다.")
    void cannotParseIdFromInvalidToken() {
        // given
        String token = "invalid token";

        // when & then
        assertThatThrownBy(() -> jwtParser.getId(token)).isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("올바르지 않은 형식의 토큰에서는 Role을 파싱할 수 없습니다.")
    void cannotParseRoleFromInvalidToken() {
        // given
        String token = "invalid token";

        // when & then
        assertThat(jwtParser.isValid(token)).isFalse();
        assertThatThrownBy(() -> jwtParser.getRole(token)).isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("올바르지 않은 형식의 토큰에서는 expiration을 파싱할 수 없습니다.")
    void cannotParseExpirationFromInvalidToken() {
        // given
        String token = "invalid token";

        // when & then
        assertThat(jwtParser.isValid(token)).isFalse();
        assertThatThrownBy(() -> jwtParser.getExpiration(token)).isInstanceOf(JwtException.class);
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
        assertThatThrownBy(() -> jwtParser.getExpiration(token)).isInstanceOf(ExpiredJwtException.class);
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
        assertThatThrownBy(() -> jwtParser.getExpiration(token)).isInstanceOf(ExpiredJwtException.class);
    }
}
