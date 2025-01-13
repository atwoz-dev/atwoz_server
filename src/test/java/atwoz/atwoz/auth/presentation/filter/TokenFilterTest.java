package atwoz.atwoz.auth.presentation.filter;

import atwoz.atwoz.auth.application.AuthErrorStatus;
import atwoz.atwoz.auth.application.AuthResponse;
import atwoz.atwoz.auth.application.AuthService;
import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.common.enums.Role;
import atwoz.atwoz.common.enums.StatusType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenFilterTest {

    private static final String EXCLUDED_PATH = "/admin/login";
    private static final String PROTECTED_PATH = "/admin";

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    private static final String VALID_ACCESS_TOKEN = "validAccessToken";
    private static final String VALID_REFRESH_TOKEN = "validRefreshToken";

    private static final String INVALID_ACCESS_TOKEN = "invalidAccessToken";
    private static final String INVALID_REFRESH_TOKEN = "invalidRefreshToken";

    private static final String REISSUED_ACCESS_TOKEN = "reissuedAccessToken";
    private static final String REISSUED_REFRESH_TOKEN = "reissuedRefreshToken";

    private static final String EXPIRED_ACCESS_TOKEN = "expiredAccessToken";
    private static final String EXPIRED_REFRESH_TOKEN = "expiredRefreshToken";

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private PathMatcherHelper pathMatcherHelper;

    @Mock
    private TokenExtractor tokenExtractor;

    @Mock
    private AuthService authService;

    @Mock
    private AuthContext authContext;

    @Mock
    private ResponseHandler responseHandler;

    @InjectMocks
    private TokenFilter tokenFilter;

    @Test
    @DisplayName("제외된 URI는 토큰을 검증하지 않고 필터를 통과합니다.")
    void shouldPassExcludedUriWithoutCheckingToken() throws IOException, ServletException {
        // given
        when(request.getRequestURI()).thenReturn(EXCLUDED_PATH);
        when(pathMatcherHelper.isExcluded(EXCLUDED_PATH)).thenReturn(true);

        // when
        tokenFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(tokenExtractor, authService, authContext, responseHandler);
    }

    @Test
    @DisplayName("Access token이 유효한 경우 AuthContext를 세팅하고 필터를 통과합니다.")
    void shouldSetAuthContextAndPassFilterWhenAccessTokenIsValid() throws IOException, ServletException {
        // given
        when(request.getRequestURI()).thenReturn(PROTECTED_PATH);
        when(pathMatcherHelper.isExcluded(PROTECTED_PATH)).thenReturn(false);

        when(tokenExtractor.extractAccessToken(request)).thenReturn(VALID_ACCESS_TOKEN);
        when(tokenExtractor.extractRefreshToken(request)).thenReturn(null);

        when(authService.authenticate(VALID_ACCESS_TOKEN, null))
                .thenReturn(AuthResponse.authenticated(1L, Role.MEMBER));

        // when
        tokenFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(authContext).authenticate(1L, Role.MEMBER);
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(responseHandler);
    }

    @Test
    @DisplayName("Access token은 만료됐지만 refresh token이 유효한 경우 토큰을 재발급합니다.")
    void shouldReissueTokensWhenAccessTokenExpiredButRefreshTokenIsValid() throws IOException, ServletException {
        // given
        when(request.getRequestURI()).thenReturn(PROTECTED_PATH);
        when(pathMatcherHelper.isExcluded(PROTECTED_PATH)).thenReturn(false);

        when(tokenExtractor.extractAccessToken(request)).thenReturn(EXPIRED_ACCESS_TOKEN);
        when(tokenExtractor.extractRefreshToken(request)).thenReturn(VALID_REFRESH_TOKEN);

        when(authService.authenticate(EXPIRED_ACCESS_TOKEN, VALID_REFRESH_TOKEN))
                .thenReturn(AuthResponse.reissued(1L, Role.MEMBER, REISSUED_ACCESS_TOKEN, REISSUED_REFRESH_TOKEN));

        // when
        tokenFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(response).setHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + REISSUED_ACCESS_TOKEN);

        ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(captor.capture());
        Cookie refreshTokenCookie = captor.getValue();

        assertThat(refreshTokenCookie.getName()).isEqualTo(REFRESH_TOKEN_COOKIE_NAME);
        assertThat(refreshTokenCookie.getValue()).isEqualTo(REISSUED_REFRESH_TOKEN);
        assertThat(refreshTokenCookie.getPath()).isEqualTo("/");
        assertThat(refreshTokenCookie.isHttpOnly()).isTrue();

        verifyNoInteractions(filterChain, authContext);
    }

    @Test
    @DisplayName("Access token이 헤더에 없는 경우 401을 반환합니다.")
    void shouldReturnUnauthorizedIfAccessTokenIsMissing() throws IOException, ServletException {
        // given
        when(request.getRequestURI()).thenReturn(PROTECTED_PATH);
        when(pathMatcherHelper.isExcluded(PROTECTED_PATH)).thenReturn(false);

        when(tokenExtractor.extractAccessToken(request)).thenReturn(null);
        when(tokenExtractor.extractRefreshToken(request)).thenReturn(null);

        when(authService.authenticate(null, null))
                .thenReturn(AuthResponse.error(AuthErrorStatus.MISSING_ACCESS_TOKEN));

        // when
        tokenFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(responseHandler).setResponse(response, StatusType.MISSING_ACCESS_TOKEN);
        verifyNoInteractions(authContext, filterChain);
    }

    @Test
    @DisplayName("Access token이 유효하지 않은 경우 401을 반환합니다.")
    void shouldReturnUnauthorizedWhenAccessTokenIsInvalid() throws IOException, ServletException {
        // given
        when(request.getRequestURI()).thenReturn(PROTECTED_PATH);
        when(pathMatcherHelper.isExcluded(PROTECTED_PATH)).thenReturn(false);

        when(tokenExtractor.extractAccessToken(request)).thenReturn(INVALID_ACCESS_TOKEN);
        when(tokenExtractor.extractRefreshToken(request)).thenReturn(VALID_REFRESH_TOKEN);

        when(authService.authenticate(INVALID_ACCESS_TOKEN, VALID_REFRESH_TOKEN))
                .thenReturn(AuthResponse.error(AuthErrorStatus.INVALID_ACCESS_TOKEN));

        // when
        tokenFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(responseHandler).setResponse(response, StatusType.INVALID_ACCESS_TOKEN);
        verifyNoInteractions(authContext, filterChain);
    }

    @Test
    @DisplayName("Access token이 만료되었지만 refresh token이 쿠키에 없는 경우 401을 반환합니다.")
    void shouldReturnUnauthorizedWhenAccessTokenIsExpiredButRefreshTokenIsMissing() throws IOException, ServletException {
        // given
        when(request.getRequestURI()).thenReturn(PROTECTED_PATH);
        when(pathMatcherHelper.isExcluded(PROTECTED_PATH)).thenReturn(false);

        when(tokenExtractor.extractAccessToken(request)).thenReturn(EXPIRED_ACCESS_TOKEN);
        when(tokenExtractor.extractRefreshToken(request)).thenReturn(null);

        when(authService.authenticate(EXPIRED_ACCESS_TOKEN, null))
                .thenReturn(AuthResponse.error(AuthErrorStatus.MISSING_REFRESH_TOKEN));

        // when
        tokenFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(responseHandler).setResponse(response, StatusType.MISSING_REFRESH_TOKEN);
        verifyNoInteractions(authContext, filterChain);
    }

    @Test
    @DisplayName("Access token이 만료되었지만 refresh token이 유효하지 않은 경우 401을 반환합니다.")
    void shouldReturnUnauthorizedWhenAccessTokenIsExpiredButRefreshTokenIsInvalid() throws IOException, ServletException {
        // given
        when(request.getRequestURI()).thenReturn(PROTECTED_PATH);
        when(pathMatcherHelper.isExcluded(PROTECTED_PATH)).thenReturn(false);

        when(tokenExtractor.extractAccessToken(request)).thenReturn(EXPIRED_ACCESS_TOKEN);
        when(tokenExtractor.extractRefreshToken(request)).thenReturn(INVALID_REFRESH_TOKEN);

        when(authService.authenticate(EXPIRED_ACCESS_TOKEN, INVALID_REFRESH_TOKEN))
                .thenReturn(AuthResponse.error(AuthErrorStatus.INVALID_REFRESH_TOKEN));

        // when
        tokenFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(responseHandler).setResponse(response, StatusType.INVALID_REFRESH_TOKEN);
        verifyNoInteractions(authContext, filterChain);
    }

    @Test
    @DisplayName("Access token이 만료되고, refresh token도 만료된 경우 401을 반환합니다.")
    void shouldReturnUnauthorizedWhenBothTokensAreExpired() throws IOException, ServletException {
        // given
        when(request.getRequestURI()).thenReturn(PROTECTED_PATH);
        when(pathMatcherHelper.isExcluded(PROTECTED_PATH)).thenReturn(false);

        when(tokenExtractor.extractAccessToken(request)).thenReturn(EXPIRED_ACCESS_TOKEN);
        when(tokenExtractor.extractRefreshToken(request)).thenReturn(EXPIRED_REFRESH_TOKEN);

        when(authService.authenticate(EXPIRED_ACCESS_TOKEN, EXPIRED_REFRESH_TOKEN))
                .thenReturn(AuthResponse.error(AuthErrorStatus.EXPIRED_REFRESH_TOKEN));

        // when
        tokenFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(responseHandler).setResponse(response, StatusType.EXPIRED_REFRESH_TOKEN);
        verifyNoInteractions(authContext, filterChain);
    }
}
