package atwoz.atwoz.common.auth.filter;


import atwoz.atwoz.common.auth.context.AuthContext;
import atwoz.atwoz.common.auth.context.Role;
import atwoz.atwoz.common.auth.filter.extractor.AccessTokenExtractor;
import atwoz.atwoz.common.auth.filter.extractor.RefreshTokenExtractor;
import atwoz.atwoz.common.auth.filter.response.ResponseHandler;
import atwoz.atwoz.common.auth.jwt.JwtParser;
import atwoz.atwoz.common.auth.jwt.JwtProvider;
import atwoz.atwoz.common.auth.jwt.JwtRepository;
import atwoz.atwoz.common.presentation.StatusType;
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

import static atwoz.atwoz.common.presentation.StatusType.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {

    private static final List<String> EXCLUDED_URIS = List.of(
            "/member/auth/login", "/member/auth/logout",
            "/admin/login", "/admin/signup",
            "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**", "/swagger-resources/**", "/webjars/**"
    );
    private final PathMatcherHelper pathMatcher = new PathMatcherHelper(EXCLUDED_URIS);
    private final JwtProvider jwtProvider;
    private final JwtParser jwtParser;
    private final JwtRepository jwtRepository;
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
            setUnauthorizedResponse(response, MISSING_ACCESS_TOKEN);
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

        setUnauthorizedResponse(response, INVALID_ACCESS_TOKEN);
    }

    private boolean isExcluded(String uri) {
        return pathMatcher.isExcluded(uri);
    }

    private boolean isValid(String token) {
        return jwtParser.isValid(token);
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
            setUnauthorizedResponse(response, MISSING_REFRESH_TOKEN);
            return;
        }

        String refreshToken = optionalRefreshToken.get();

        if (isValid(refreshToken) && jwtRepository.isExists(refreshToken)) {
            String reissuedAccessToken = reissueAccessToken(refreshToken);
            addAccessTokenToHeader(response, reissuedAccessToken);

            String reissuedRefreshToken = reissueRefreshToken(refreshToken);
            jwtRepository.save(reissuedRefreshToken);
            addRefreshTokenToCookie(response, reissuedRefreshToken);
            return;
        }

        // TODO: 기존 refresh token 무효화 메서드 구현
        invalidateRefreshToken(refreshToken);
        setUnauthorizedResponse(response, INVALID_REFRESH_TOKEN);
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
        invalidateRefreshToken(token);
        return jwtProvider.createRefreshToken(id, role, Instant.now());
    }

    private String reissueAccessToken(String token) {
        Long id = jwtParser.getIdFrom(token);
        Role role = jwtParser.getRoleFrom(token);
        return jwtProvider.createAccessToken(id, role, Instant.now());
    }

    private void invalidateRefreshToken(String refreshToken) {
        jwtRepository.delete(refreshToken);
    }

    private void setUnauthorizedResponse(HttpServletResponse response, StatusType statusType) {
        log.error("토큰 인증에 실패했습니다: {}", statusType.getMessage());
        responseHandler.setResponse(response, statusType);
    }
}