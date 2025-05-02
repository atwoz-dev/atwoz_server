package atwoz.atwoz.auth.application;

import atwoz.atwoz.auth.domain.TokenParser;
import atwoz.atwoz.auth.domain.TokenProvider;
import atwoz.atwoz.auth.domain.TokenRepository;
import atwoz.atwoz.common.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private TokenParser tokenParser;

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Access token이 null인 경우 MISSING_ACCESS_TOKEN 에러를 반환합니다.")
    void shouldReturnErrorWhenAccessTokenIsNull() {
        // given
        String accessToken = null;
        String refreshToken = "validRefreshToken";

        // when
        AuthResponse response = authService.authenticate(accessToken, refreshToken);

        // then
        assertThat(response.isError()).isTrue();
        assertThat(response.getErrorStatus()).isEqualTo(AuthErrorStatus.MISSING_ACCESS_TOKEN);
    }

    @Test
    @DisplayName("Access token이 유효한 경우 id와 role을 반환합니다.")
    void shouldReturnIdAndRoleWhenAccessTokenIsValid() {
        // given
        String accessToken = "validAccessToken";
        String refreshToken = "validRefreshToken";

        when(tokenParser.isValid(accessToken)).thenReturn(true);
        when(tokenParser.getId(accessToken)).thenReturn(1L);
        when(tokenParser.getRole(accessToken)).thenReturn(Role.MEMBER);

        // when
        AuthResponse response = authService.authenticate(accessToken, refreshToken);

        // then
        assertThat(response.isAuthenticated()).isTrue();
        assertThat(response.getMemberId()).isEqualTo(1L);
        assertThat(response.getRole()).isEqualTo(Role.MEMBER);
    }

    @Test
    @DisplayName("Access token이 유효하지 않은 경우 INVALID_ACCESS_TOKEN 에러를 반환합니다.")
    void shouldReturnErrorWhenAccessTokenIsInvalid() {
        // given
        String accessToken = "invalidAccessToken";
        String refreshToken = "validRefreshToken";

        when(tokenParser.isValid(accessToken)).thenReturn(false);
        when(tokenParser.isExpired(accessToken)).thenReturn(false);

        // when
        AuthResponse response = authService.authenticate(accessToken, refreshToken);

        // then
        assertThat(response.isError()).isTrue();
        assertThat(response.getErrorStatus()).isEqualTo(AuthErrorStatus.INVALID_ACCESS_TOKEN);
    }

    @Nested
    @DisplayName("Access token 만료")
    class ExpiredAccessToken {

        private static final String ACCESS_TOKEN = "accessToken";
        private static final String REFRESH_TOKEN = "refreshToken";

        @BeforeEach
        void setUp() {
            when(tokenParser.isValid(ACCESS_TOKEN)).thenReturn(false);
            when(tokenParser.isExpired(ACCESS_TOKEN)).thenReturn(true);
        }

        @Test
        @DisplayName("Refresh token이 null인 경우 MISSING_REFRESH_TOKEN 에러를 반환합니다.")
        void shouldReturnErrorWhenRefreshTokenIsNull() {
            // given
            String refreshToken = null;

            // when
            AuthResponse response = authService.authenticate(ACCESS_TOKEN, refreshToken);

            // then
            assertThat(response.isError()).isTrue();
            assertThat(response.getErrorStatus()).isEqualTo(AuthErrorStatus.MISSING_REFRESH_TOKEN);
        }

        @Test
        @DisplayName("Refresh token이 만료된 경우 기존 refresh token을 제거하고, EXPIRED_REFRESH_TOKEN 에러를 반환합니다.")
        void shouldInvalidateTokenAndReturnErrorWhenRefreshTokenIsExpired() {
            // given
            when(tokenParser.isExpired(REFRESH_TOKEN)).thenReturn(true);

            // when
            AuthResponse response = authService.authenticate(ACCESS_TOKEN, REFRESH_TOKEN);

            // then
            verify(tokenRepository).delete(REFRESH_TOKEN);
            assertThat(response.isError()).isTrue();
            assertThat(response.getErrorStatus()).isEqualTo(AuthErrorStatus.EXPIRED_REFRESH_TOKEN);
        }

        @Test
        @DisplayName("Refresh token이 유효하지 않은 경우 INVALID_REFRESH_TOKEN 에러를 반환합니다.")
        void shouldInvalidateTokenAndReturnErrorWhenRefreshTokenIsInvalid() {
            // given
            when(tokenParser.isValid(REFRESH_TOKEN)).thenReturn(false);
            when(tokenParser.isExpired(REFRESH_TOKEN)).thenReturn(false);

            // when
            AuthResponse response = authService.authenticate(ACCESS_TOKEN, REFRESH_TOKEN);

            // then
            assertThat(response.isError()).isTrue();
            assertThat(response.getErrorStatus()).isEqualTo(AuthErrorStatus.INVALID_REFRESH_TOKEN);
        }

        @Test
        @DisplayName("Refresh token이 저장소에 존재하지 않는 경우 INVALID_REFRESH_TOKEN 에러를 반환합니다.")
        void shouldInvalidateTokenAndReturnErrorWhenRefreshTokenIsMissing() {
            // given
            when(tokenParser.isValid(REFRESH_TOKEN)).thenReturn(true);
            when(tokenParser.isExpired(REFRESH_TOKEN)).thenReturn(false);
            when(tokenRepository.exists(REFRESH_TOKEN)).thenReturn(false);

            // when
            AuthResponse response = authService.authenticate(ACCESS_TOKEN, REFRESH_TOKEN);

            // then
            assertThat(response.isError()).isTrue();
            assertThat(response.getErrorStatus()).isEqualTo(AuthErrorStatus.INVALID_REFRESH_TOKEN);
        }

        @Test
        @DisplayName("Refresh token이 유효한 경우 기존 refresh token을 제거하고, 새로운 access token과 refresh token을 재발급해 반환합니다.")
        void shouldInvalidateTokenAndReturnReissuedTokensWhenRefreshTokenIsValid() {
            // given
            when(tokenParser.isValid(REFRESH_TOKEN)).thenReturn(true);
            when(tokenParser.isExpired(REFRESH_TOKEN)).thenReturn(false);
            when(tokenRepository.exists(REFRESH_TOKEN)).thenReturn(true);

            when(tokenParser.getId(REFRESH_TOKEN)).thenReturn(1L);
            when(tokenParser.getRole(REFRESH_TOKEN)).thenReturn(Role.MEMBER);

            String reissuedAccessToken = "reissuedAccessToken";
            String reissuedRefreshToken = "reissuedRefreshToken";

            when(tokenProvider.createAccessToken(eq(1L), eq(Role.MEMBER), any(Instant.class)))
                .thenReturn(reissuedAccessToken);
            when(tokenProvider.createRefreshToken(eq(1L), eq(Role.MEMBER), any(Instant.class)))
                .thenReturn(reissuedRefreshToken);

            // when
            AuthResponse response = authService.authenticate(ACCESS_TOKEN, REFRESH_TOKEN);

            // then
            verify(tokenRepository).delete(REFRESH_TOKEN);
            verify(tokenProvider).createAccessToken(eq(1L), eq(Role.MEMBER), any(Instant.class));
            verify(tokenProvider).createRefreshToken(eq(1L), eq(Role.MEMBER), any(Instant.class));
            verify(tokenRepository).save(reissuedRefreshToken);

            assertThat(response.isReissued()).isTrue();
            assertThat(response.getAccessToken()).isEqualTo(reissuedAccessToken);
            assertThat(response.getRefreshToken()).isEqualTo(reissuedRefreshToken);
        }
    }
}
