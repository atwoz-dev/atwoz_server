package atwoz.atwoz.common.auth.filter;

import atwoz.atwoz.auth.context.AuthContext;
import atwoz.atwoz.auth.context.Role;
import atwoz.atwoz.auth.filter.TokenFilter;
import atwoz.atwoz.auth.filter.response.ResponseHandler;
import atwoz.atwoz.auth.jwt.JwtParser;
import atwoz.atwoz.auth.jwt.JwtProvider;
import atwoz.atwoz.auth.jwt.JwtRepository;
import atwoz.atwoz.common.presentation.StatusType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Instant;

import static atwoz.atwoz.common.presentation.StatusType.MISSING_REFRESH_TOKEN;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenFilterTest {

    private static final String EXCLUDED_PATH = "/admin/login";
    private static final String PROTECTED_PATH = "/protected";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private static final String VALID_ACCESS_TOKEN = "validAccessToken";
    private static final String INVALID_ACCESS_TOKEN = "invalidAccessToken";

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private JwtRepository jwtRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private JwtParser jwtParser;

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

        // when
        tokenFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtProvider, jwtParser, authContext, responseHandler);
    }

    @Test
    @DisplayName("Access token이 헤더에 없는 경우 401을 반환합니다.")
    void shouldReturnUnauthorizedIfAccessTokenIsMissing() throws IOException, ServletException {
        // given
        when(request.getRequestURI()).thenReturn(PROTECTED_PATH);
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(null);

        // when
        tokenFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(responseHandler).setResponse(response, StatusType.MISSING_ACCESS_TOKEN);
        verifyNoInteractions(jwtProvider, jwtParser, authContext, filterChain);
    }


    @Test
    @DisplayName("Access token이 유효한 경우 AuthContext를 세팅하고 필터를 통과합니다.")
    void shouldSetAuthContextAndPassFilterWhenAccessTokenIsValid() throws IOException, ServletException {
        // given
        when(request.getRequestURI()).thenReturn(PROTECTED_PATH);
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + VALID_ACCESS_TOKEN);

        when(jwtParser.isValid(VALID_ACCESS_TOKEN)).thenReturn(true);
        when(jwtParser.getIdFrom(VALID_ACCESS_TOKEN)).thenReturn(1L);
        when(jwtParser.getRoleFrom(VALID_ACCESS_TOKEN)).thenReturn(Role.MEMBER);

        // when
        tokenFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(authContext).authenticate(1L, Role.MEMBER);
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtProvider, responseHandler);
    }

    @Test
    @DisplayName("Access token이 유효하지 않은 경우 401을 반환합니다.")
    void shouldReturnUnauthorizedIfAccessTokenIsInvalid() throws IOException, ServletException {
        // given
        when(request.getRequestURI()).thenReturn(PROTECTED_PATH);
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + INVALID_ACCESS_TOKEN);

        when(jwtParser.isValid(INVALID_ACCESS_TOKEN)).thenReturn(false);
        when(jwtParser.isExpired(INVALID_ACCESS_TOKEN)).thenReturn(false);

        // when
        tokenFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(responseHandler).setResponse(response, StatusType.INVALID_ACCESS_TOKEN);
        verifyNoInteractions(jwtProvider, authContext, filterChain);
    }

    @Nested
    @DisplayName("Access token 만료 케이스")
    class AccessTokenExpired {

        private static final String VALID_REFRESH_TOKEN = "validRefreshToken";
        private static final String INVALID_REFRESH_TOKEN = "invalidRefreshToken";
        private static final String EXPIRED_ACCESS_TOKEN = "expiredAccessToken";
        private static final String EXPIRED_REFRESH_TOKEN = "expiredRefreshToken";
        private static final String REISSUED_ACCESS_TOKEN = "reissuedAccessToken";
        private static final String REISSUED_REFRESH_TOKEN = "reissuedRefreshToken";
        private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

        @BeforeEach
        void setUp() {
            when(request.getRequestURI()).thenReturn(PROTECTED_PATH);
            when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + EXPIRED_ACCESS_TOKEN);

            when(jwtParser.isValid(EXPIRED_ACCESS_TOKEN)).thenReturn(false);
            when(jwtParser.isExpired(EXPIRED_ACCESS_TOKEN)).thenReturn(true);
        }

        @Test
        @DisplayName("Refresh token이 쿠키에 없는 경우 401을 반환합니다.")
        void shouldReturnUnauthorizedIfRefreshTokenIsMissing() throws IOException, ServletException {
            // given
            when(request.getCookies()).thenReturn(new Cookie[0]);

            // when
            tokenFilter.doFilterInternal(request, response, filterChain);

            // then
            verify(responseHandler).setResponse(response, MISSING_REFRESH_TOKEN);
            verifyNoInteractions(jwtProvider, authContext, filterChain);
        }

        @Test
        @DisplayName("Refresh token이 유효한 경우 access token과 refresh token을 재발급합니다.")
        void shouldReissueTokensWhenAccessTokenIsExpiredAndRefreshTokenIsValid() throws IOException, ServletException {
            // given
            when(request.getCookies()).thenReturn(new Cookie[]{new Cookie(REFRESH_TOKEN_COOKIE_NAME, VALID_REFRESH_TOKEN)});

            long id = 1L;
            Role role = Role.MEMBER;

            when(jwtParser.isValid(VALID_REFRESH_TOKEN)).thenReturn(true);
            when(jwtParser.getIdFrom(VALID_REFRESH_TOKEN)).thenReturn(id);
            when(jwtParser.getRoleFrom(VALID_REFRESH_TOKEN)).thenReturn(role);

            when(jwtProvider.createAccessToken(eq(id), eq(role), any(Instant.class))).thenReturn(REISSUED_ACCESS_TOKEN);
            when(jwtProvider.createRefreshToken(eq(id), eq(role), any(Instant.class))).thenReturn(REISSUED_REFRESH_TOKEN);
            when(jwtRepository.isExists(VALID_REFRESH_TOKEN)).thenReturn(true);

            // when
            tokenFilter.doFilterInternal(request, response, filterChain);

            // then
            verify(response).setHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + REISSUED_ACCESS_TOKEN);
            verify(response).addCookie(
                    argThat(cookie -> REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()) && REISSUED_REFRESH_TOKEN.equals(cookie.getValue()))
            );
            verifyNoInteractions(authContext, responseHandler, filterChain);
        }

        @Test
        @DisplayName("Refresh token이 유효하지 않은 경우 토큰을 무효화하고 401을 반환합니다.")
        void shouldInvalidateTokenAndReturnUnauthorizedIfRefreshTokenIsInvalid() throws IOException, ServletException {
            // given
            when(request.getCookies()).thenReturn(new Cookie[]{new Cookie(REFRESH_TOKEN_COOKIE_NAME, INVALID_REFRESH_TOKEN)});
            when(jwtParser.isValid(INVALID_REFRESH_TOKEN)).thenReturn(false);

            // when
            tokenFilter.doFilterInternal(request, response, filterChain);

            // then
            verify(responseHandler).setResponse(response, StatusType.INVALID_REFRESH_TOKEN);
            verifyNoInteractions(jwtProvider, authContext, filterChain);
        }

        @Test
        @DisplayName("Refresh Token이 유효하지만, 화이트리스트로 등록되지 않은 경우, 401을 반환합니다.")
        void returnUnauthorizedIfRefreshTokenIsNotInWhiteList() throws IOException, ServletException {
            // given
            when(request.getCookies()).thenReturn(new Cookie[]{new Cookie(REFRESH_TOKEN_COOKIE_NAME, VALID_REFRESH_TOKEN)});
            when(jwtParser.isValid(VALID_REFRESH_TOKEN)).thenReturn(true);
            when(jwtRepository.isExists(VALID_REFRESH_TOKEN)).thenReturn(false);

            // when
            tokenFilter.doFilterInternal(request, response, filterChain);

            // then
            verify(responseHandler).setResponse(response, StatusType.INVALID_REFRESH_TOKEN);
            verifyNoInteractions(jwtProvider, authContext, filterChain);
        }

        @Test
        @DisplayName("Refresh token이 만료된 경우 토큰을 무효화하고 401을 반환합니다.")
        void shouldInvalidateTokenAndReturnUnauthorizedIfRefreshTokenIsExpired() throws IOException, ServletException {
            // given
            when(request.getCookies()).thenReturn(new Cookie[]{new Cookie(REFRESH_TOKEN_COOKIE_NAME, EXPIRED_REFRESH_TOKEN)});
            when(jwtParser.isValid(EXPIRED_REFRESH_TOKEN)).thenReturn(true);

            // when
            tokenFilter.doFilterInternal(request, response, filterChain);

            // then
            verify(responseHandler).setResponse(response, StatusType.INVALID_REFRESH_TOKEN);
            verifyNoInteractions(jwtProvider, authContext, filterChain);
        }
    }
}
