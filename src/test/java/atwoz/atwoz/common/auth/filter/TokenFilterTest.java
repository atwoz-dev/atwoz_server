package atwoz.atwoz.common.auth.filter;

import atwoz.atwoz.common.auth.context.AuthContext;
import atwoz.atwoz.common.auth.context.Role;
import atwoz.atwoz.common.auth.jwt.JwtParser;
import atwoz.atwoz.common.auth.jwt.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenFilterTest {

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private JwtParser jwtParser;

    @Mock
    private AuthContext authContext;

    @Mock
    private ResponseHandler responseHandler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private TokenFilter tokenFilter;

    @Test
    @DisplayName("제외된 URI는 토큰 검증 로직을 수행하지 않습니다.")
    void shouldPassExcludedUriWithoutCheckingToken() throws IOException, ServletException {
        // given
        when(request.getRequestURI()).thenReturn("/admin/login");

        // when
        tokenFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtProvider, jwtParser, responseHandler, authContext);
    }

    @Test
    @DisplayName("Access token이 헤더에 없는 경우 401 응답을 반환합니다.")
    void shouldReturnUnauthorizedIfAccessTokenIsMissing() throws IOException, ServletException {
        // given
        when(request.getRequestURI()).thenReturn("/protected/resource");
        when(request.getHeader("Authorization")).thenReturn(null);

        // when
        tokenFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(responseHandler).setResponse(response, ResponseHandler.StatusCode.UNAUTHORIZED, "Access token이 존재하지 않습니다.");
        verifyNoInteractions(jwtProvider, jwtParser, authContext);
        verifyNoInteractions(filterChain);
    }


    @Test
    @DisplayName("Access token이 유효한 경우 AuthContext를 세팅하고 필터를 통과합니다.")
    void shouldSetAuthContextAndPassFilterWhenAccessTokenIsValid() throws IOException, ServletException {
        // given
        String validAccessToken = "validAccessToken";
        Long id = 1L;
        Role role = Role.MEMBER;

        when(request.getRequestURI()).thenReturn("/protected/resource");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validAccessToken);

        when(jwtParser.isValid(validAccessToken)).thenReturn(true);
        when(jwtParser.getIdFrom(validAccessToken)).thenReturn(id);
        when(jwtParser.getRoleFrom(validAccessToken)).thenReturn(role);

        // when
        tokenFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(authContext).authenticate(id, role);
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(responseHandler);
    }

    @Test
    @DisplayName("Access token이 유효하지 않은 경우 401 응답을 반환합니다.")
    void shouldReturnUnauthorizedIfAccessTokenIsInvalid() throws IOException, ServletException {
        // given
        String invalidAccessToken = "invalidAccessToken";

        when(request.getRequestURI()).thenReturn("/protected/resource");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidAccessToken);

        when(jwtParser.isValid(invalidAccessToken)).thenReturn(false);
        when(jwtParser.isExpired(invalidAccessToken)).thenReturn(false);

        // when
        tokenFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(responseHandler).setResponse(response, ResponseHandler.StatusCode.UNAUTHORIZED, "유효하지 않은 access token입니다.");
        verifyNoInteractions(authContext, filterChain);
    }

    @Test
    @DisplayName("Access token이 만료되었지만 refresh token이 유효한 경우 토큰을 재발급합니다.")
    void shouldReissueTokensWhenAccessTokenIsExpiredAndRefreshTokenIsValid() throws IOException, ServletException {
        // given
        String expiredAccessToken = "expiredAccessToken";
        String validRefreshToken = "validRefreshToken";
        String reissuedAccessToken = "reissuedAccessToken";
        String reissuedRefreshToken = "reissuedRefreshToken";

        when(request.getRequestURI()).thenReturn("/protected/resource");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + expiredAccessToken);
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("refresh_token", validRefreshToken)});

        when(jwtParser.isValid(expiredAccessToken)).thenReturn(false);
        when(jwtParser.isExpired(expiredAccessToken)).thenReturn(true);
        when(jwtParser.isValid(validRefreshToken)).thenReturn(true);
        when(jwtParser.getIdFrom(validRefreshToken)).thenReturn(1L);
        when(jwtParser.getRoleFrom(validRefreshToken)).thenReturn(Role.MEMBER);

        when(jwtProvider.createAccessToken(anyLong(), any(Role.class), any())).thenReturn(reissuedAccessToken);
        when(jwtProvider.createRefreshToken(anyLong(), any(Role.class), any())).thenReturn(reissuedRefreshToken);

        // when
        tokenFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(response).setHeader("Authorization", "Bearer " + reissuedAccessToken);
        verify(response).addCookie(
                argThat(cookie -> "refresh_token".equals(cookie.getName()) && reissuedRefreshToken.equals(cookie.getValue()))
        );
        verifyNoInteractions(responseHandler, filterChain);
    }

    // TODO: Refresh token 만료 case
}
