package atwoz.atwoz.common.infra;

import atwoz.atwoz.common.auth.domain.Role;
import atwoz.atwoz.common.auth.infra.JwtProvider;
import atwoz.atwoz.common.auth.infra.exception.TokenExpiredException;
import atwoz.atwoz.common.auth.infra.exception.TokenFormInvalidException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class JwtProviderTest {

    private JwtProvider jwtProvider;
    private JwtProvider shortTimeJwtProvider;

    private String secret = "this-is-secret-key-value-at-least-128-bytes";
    private int accessTokenExpirationTime = 86400;
    private int refreshTokenExpirationTime = 86400;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(secret, accessTokenExpirationTime, refreshTokenExpirationTime);
        jwtProvider.init();

        shortTimeJwtProvider = new JwtProvider(secret, 0, refreshTokenExpirationTime);
        shortTimeJwtProvider.init();
    }

    @Test
    @DisplayName("유효한 값들을 사용하여 토큰을 생성합니다.")
    void createTokenWithValidValues() {
        // Given
        Long id = 1L;
        Role role = Role.MEMBER;

        // When
        String token = jwtProvider.createAccessToken(1L, role);

        // Then
        Assertions.assertThat(token).isNotNull();
        Assertions.assertThat(jwtProvider.extractId(token)).isEqualTo(id);
        Assertions.assertThat(jwtProvider.extractRole(token)).isEqualTo(role);
    }

    @Test
    @DisplayName("유효하지 않은 토큰을 사용하여 클레임을 추출하면 예외를 발생시킵니다.")
    void throwErrorWhenInvalidToken() {
        // Given
        String token = "thisisinvalidtoken";

        // When & Then
        Assertions.assertThatThrownBy(() -> jwtProvider.extractId(token)).isInstanceOf(TokenFormInvalidException.class);
        Assertions.assertThatThrownBy(() -> jwtProvider.extractRole(token)).isInstanceOf(TokenFormInvalidException.class);
    }

    @Test
    @DisplayName("유효기간이 만료된 토큰에서 클레임을 추출하면 예외를 발생시킵니다.")
    void throwErrorWhenExpiredToken() {
        // Given
        String token = shortTimeJwtProvider.createAccessToken(1L, Role.MEMBER);

        // When & Then
        Assertions.assertThatThrownBy(() -> jwtProvider.extractId(token)).isInstanceOf(TokenExpiredException.class);
    }
}
