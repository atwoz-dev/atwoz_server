package atwoz.atwoz.auth.infra.filter;


import atwoz.atwoz.auth.domain.Role;
import atwoz.atwoz.auth.domain.TokenParser;
import atwoz.atwoz.auth.domain.TokenProvider;
import atwoz.atwoz.auth.domain.TokenRepository;
import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.common.StatusType;
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

import static atwoz.atwoz.common.StatusType.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {

    private static final List<String> EXCLUDED_URIS = List.of(
            "/member/auth/login", "/member/auth/logout",
            "/admin/login", "/admin/signup",
            "/v3/api-docs/**", "/config-ui.html", "/config-ui/**", "/config-resources/**", "/webjars/**"
    );
    private final PathMatcherHelper pathMatcher = new PathMatcherHelper(EXCLUDED_URIS);
    private final TokenProvider tokenProvider;
    private final TokenParser tokenParser;
    private final TokenRepository tokenRepository;
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
        return tokenParser.isValid(token);
    }

    private boolean isExpired(String token) {
        return tokenParser.isExpired(token);
    }

    private void setAuthenticationContext(String accessToken) {
        Long id = tokenParser.getId(accessToken);
        Role role = tokenParser.getRole(accessToken);
        authContext.authenticate(id, role);
    }

    private void handleExpiredAccessToken(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> optionalRefreshToken = RefreshTokenExtractor.extractFrom(request);

        if (optionalRefreshToken.isEmpty()) {
            setUnauthorizedResponse(response, MISSING_REFRESH_TOKEN);
            return;
        }

        String refreshToken = optionalRefreshToken.get();

        if (isValid(refreshToken) && tokenRepository.exists(refreshToken)) {
            String reissuedAccessToken = reissueAccessToken(refreshToken);
            addAccessTokenToHeader(response, reissuedAccessToken);

            String reissuedRefreshToken = reissueRefreshToken(refreshToken);
            tokenRepository.save(reissuedRefreshToken);
            addRefreshTokenToCookie(response, reissuedRefreshToken);
            return;
        }

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
        long id = tokenParser.getId(token);
        Role role = tokenParser.getRole(token);
        invalidateRefreshToken(token);
        return tokenProvider.createRefreshToken(id, role, Instant.now());
    }

    private String reissueAccessToken(String token) {
        long id = tokenParser.getId(token);
        Role role = tokenParser.getRole(token);
        return tokenProvider.createAccessToken(id, role, Instant.now());
    }

    private void invalidateRefreshToken(String refreshToken) {
        tokenRepository.delete(refreshToken);
    }

    private void setUnauthorizedResponse(HttpServletResponse response, StatusType statusType) {
        log.error("토큰 인증에 실패했습니다: {}", statusType.getMessage());
        responseHandler.setResponse(response, statusType);
    }
}