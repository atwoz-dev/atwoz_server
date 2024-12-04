package atwoz.atwoz.common.auth.filter;


import atwoz.atwoz.common.auth.AuthContext;
import atwoz.atwoz.common.auth.Role;
import atwoz.atwoz.common.auth.exception.UnauthorizedException;
import atwoz.atwoz.common.auth.filter.extractor.AccessTokenExtractor;
import atwoz.atwoz.common.auth.filter.extractor.RefreshTokenExtractor;
import atwoz.atwoz.common.auth.jwt.JwtParser;
import atwoz.atwoz.common.auth.jwt.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
public class JwtFilter extends OncePerRequestFilter {

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

    // TODO: refresh token 재발급 로직
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isExcluded(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Optional<String> refreshToken = RefreshTokenExtractor.extractFrom(request);
            if (refreshToken.isPresent()) {
                handleRefreshToken(refreshToken.get(), response);
                return;
            }

            Optional<String> accessToken = AccessTokenExtractor.extractFrom(request);
            if (accessToken.isPresent()) {
                handleAccessToken(accessToken.get());
                filterChain.doFilter(request, response);
            }

            setUnauthorizedResponse(response, "토큰이 존재하지 않습니다.");
        } catch (UnauthorizedException e) {
            setUnauthorizedResponse(response, e.getMessage());
        }
    }

    private boolean isExcluded(String uri) {
        return pathMatcher.isExcluded(uri);
    }

    private void handleRefreshToken(String token, HttpServletResponse response) {
        if (isInvalid(token)) {
            throw new UnauthorizedException("유효하지 않은 refresh token입니다.");
        }
        String reissuedAccessToken = reissueAccessToken(token);
        setAuthorizationHeader(response, reissuedAccessToken);
    }

    private String reissueAccessToken(String token) {
        Long id = jwtParser.getIdFrom(token);
        Role role = jwtParser.getRoleFrom(token);
        return jwtProvider.createAccessToken(id, role, Instant.now());
    }

    private void setAuthorizationHeader(HttpServletResponse response, String reissuedAccessToken) {
        response.setHeader("Authorization", "Bearer " + reissuedAccessToken);
    }

    private void handleAccessToken(String token) {
        if (isInvalid(token)) {
            throw new UnauthorizedException("유효하지 않은 access token입니다.");
        }
        Long id = jwtParser.getIdFrom(token);
        Role role = jwtParser.getRoleFrom(token);
        authContext.setAuthentication(id, role);
    }

    private boolean isInvalid(String token) {
        return jwtParser.isInvalid(token);
    }

    private void setUnauthorizedResponse(HttpServletResponse response, String message) {
        log.error("토큰 인증이 실패했습니다: {}", message);
        responseHandler.setResponse(response, ResponseHandler.StatusCode.UNAUTHORIZED, message);
    }
}