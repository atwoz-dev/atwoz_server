package atwoz.atwoz.common.auth.filter;


import atwoz.atwoz.common.auth.AuthContext;
import atwoz.atwoz.common.auth.Role;
import atwoz.atwoz.common.auth.exception.TokenException;
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
        }

        try {
            Optional<String> refreshToken = RefreshTokenExtractor.extractFrom(request);
            if (refreshToken.isPresent()) {
                handleRefreshToken(response, refreshToken.get());
                return;
            }

            Optional<String> accessToken = AccessTokenExtractor.extractFrom(request);
            if (accessToken.isPresent()) {
                handleAccessToken(accessToken.get());
                filterChain.doFilter(request, response);
            }

            setUnauthorizedResponse(response, "토큰이 존재하지 않습니다.");
        } catch (TokenException e) {
            setUnauthorizedResponse(response, e.getMessage());
        }
    }

    private boolean isExcluded(String uri) {
        return pathMatcher.isExcluded(uri);
    }

    private void handleRefreshToken(HttpServletResponse response, String refreshToken) {
        if (isExpired(refreshToken)) {
            rotateRefreshToken(response, refreshToken);
            return;
        }

        if (isInvalid(refreshToken)) {
            throw new TokenException("유효하지 않은 refresh token입니다.");
        }

        rotateRefreshToken(response, refreshToken);
    }

    private void handleAccessToken(String accessToken) {
        if (isExpired(accessToken)) {
            throw new TokenException("만료된 access token입니다.");
        }

        if (isInvalid(accessToken)) {
            throw new TokenException("유효하지 않은 access token입니다.");
        }

        Long id = jwtParser.getIdFrom(accessToken);
        Role role = jwtParser.getRoleFrom(accessToken);
        authContext.setAuthentication(id, role);
    }

    private boolean isInvalid(String token) {
        return jwtParser.isInvalid(token);
    }

    private boolean isExpired(String token) {
        return jwtParser.isExpired(token);
    }

    private void rotateRefreshToken(HttpServletResponse response, String refreshToken) {
        String reissuedRefreshToken = reissueRefreshToken(refreshToken);
        addRefreshTokenToCookie(response, reissuedRefreshToken);

        String reissuedAccessToken = reissueAccessToken(refreshToken);
        addAccessTokenToHeader(response, reissuedAccessToken);
    }

    private String reissueRefreshToken(String token) {
        Long id = jwtParser.getIdFrom(token);
        Role role = jwtParser.getRoleFrom(token);
        return jwtProvider.createRefreshToken(id, role, Instant.now());
    }

    private void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setMaxAge(60 * 60 * 24 * 7 * 4);  // 4주
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);

        response.addCookie(cookie);
    }

    private String reissueAccessToken(String token) {
        Long id = jwtParser.getIdFrom(token);
        Role role = jwtParser.getRoleFrom(token);
        return jwtProvider.createAccessToken(id, role, Instant.now());
    }

    private void addAccessTokenToHeader(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Authorization", "Bearer " + accessToken);
    }

    private void setUnauthorizedResponse(HttpServletResponse response, String message) {
        log.error("토큰 인증이 실패했습니다: {}", message);
        responseHandler.setResponse(response, ResponseHandler.StatusCode.UNAUTHORIZED, message);
    }
}