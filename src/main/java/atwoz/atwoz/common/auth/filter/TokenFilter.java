package atwoz.atwoz.common.auth.filter;


import atwoz.atwoz.common.auth.context.AuthContext;
import atwoz.atwoz.common.auth.context.Role;
import atwoz.atwoz.common.auth.filter.extractor.AccessTokenExtractor;
import atwoz.atwoz.common.auth.filter.extractor.RefreshTokenExtractor;
import atwoz.atwoz.common.auth.jwt.JwtParser;
import atwoz.atwoz.common.auth.jwt.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {

    private static final List<String> EXCLUDED_URIS = List.of(
            "/members/auth/login",
            "/admin/login", "/admin/signup",
            "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**", "/swagger-resources/**", "/webjars/**"
    );
    private final PathMatcherHelper pathMatcher = new PathMatcherHelper(EXCLUDED_URIS);
    private final JwtProvider jwtProvider;
    private final JwtParser jwtParser;
    private final ResponseHandler responseHandler;
    private final AuthContext authContext;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isExcluded(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<String> optionalAccessToken = AccessTokenExtractor.extractFrom(request);

        if (optionalAccessToken.isEmpty()) {
            setUnauthorizedResponse(response, "Access token이 존재하지 않습니다.");
            return;
        }

        String accessToken = optionalAccessToken.get();

        if (isValid(accessToken)) {
            setAuthenticationContext(accessToken);
            filterChain.doFilter(request, response);
            return;
        }

        if (isExpired(accessToken)) {
            handleExpiredAccessToken(request, response);
            return;
        }

        if (isInvalid(accessToken)) {
            setUnauthorizedResponse(response, "유효하지 않은 access token입니다.");
        }
    }

    private boolean isExcluded(String uri) {
        return pathMatcher.isExcluded(uri);
    }

    private boolean isValid(String token) {
        return jwtParser.isValid(token);
    }

    private boolean isInvalid(String token) {
        return !jwtParser.isValid(token);
    }

    private boolean isExpired(String token) {
        return jwtParser.isExpired(token);
    }

    private void setAuthenticationContext(String accessToken) {
        Long id = jwtParser.getIdFrom(accessToken);
        Role role = jwtParser.getRoleFrom(accessToken);
        authContext.authenticate(id, role);
    }

    private void handleExpiredAccessToken(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> optionalRefreshToken = RefreshTokenExtractor.extractFrom(request);

        if (optionalRefreshToken.isEmpty()) {
            setUnauthorizedResponse(response, "Refresh token이 존재하지 않습니다.");
            return;
        }

        String refreshToken = optionalRefreshToken.get();

        if (isValid(refreshToken)) {
            String reissuedAccessToken = reissueAccessToken(refreshToken);
            addAccessTokenToHeader(response, reissuedAccessToken);

            String reissuedRefreshToken = reissueRefreshToken(refreshToken);
            addRefreshTokenToCookie(response, reissuedRefreshToken);
            return;
        }

        if (isInvalid(refreshToken) || isExpired(refreshToken)) {
            // TODO: 기존 refresh token 무효화 메서드 구현
            invalidateRefreshToken(refreshToken);
            setUnauthorizedResponse(response, "유효하지 않은 refresh token입니다.");
        }
    }

    private void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setMaxAge(60 * 60 * 24 * 7 * 4);  // 4주
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);

        response.addCookie(cookie);
    }

    private void addAccessTokenToHeader(HttpServletResponse response, String accessToken) {
        response.setHeader("Authorization", "Bearer " + accessToken);
    }

    private String reissueRefreshToken(String token) {
        Long id = jwtParser.getIdFrom(token);
        Role role = jwtParser.getRoleFrom(token);
        return jwtProvider.createRefreshToken(id, role, Instant.now());
    }

    private String reissueAccessToken(String token) {
        Long id = jwtParser.getIdFrom(token);
        Role role = jwtParser.getRoleFrom(token);
        return jwtProvider.createAccessToken(id, role, Instant.now());
    }

    private void invalidateRefreshToken(String refreshToken) {
        // TODO: 메서드 구현
    }

    private void setUnauthorizedResponse(HttpServletResponse response, String message) {
        log.error("토큰 인증이 실패했습니다: {}", message);
        responseHandler.setResponse(response, ResponseHandler.StatusCode.UNAUTHORIZED, message);
    }
}