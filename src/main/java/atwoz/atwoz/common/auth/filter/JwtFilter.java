package atwoz.atwoz.common.auth.filter;


import atwoz.atwoz.common.auth.*;
import atwoz.atwoz.common.auth.exception.UnauthorizedException;
import atwoz.atwoz.common.auth.jwt.JwtParser;
import atwoz.atwoz.common.auth.jwt.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final JwtParser jwtParser;
    private final AuthContext authContext;
    private final TokenExceptionHandler tokenExceptionHandler;
    private PathMatcherHelper pathMatcher;

    public JwtFilter(JwtProvider jwtProvider, JwtParser jwtParser, AuthContext authContext, TokenExceptionHandler tokenExceptionHandler) {
        setPathMatcher();
        this.jwtProvider = jwtProvider;
        this.jwtParser = jwtParser;
        this.authContext = authContext;
        this.tokenExceptionHandler = tokenExceptionHandler;
    }

    // TODO: 상황에 따라 다른 처리? (만료, 위조 등)
    // TODO: 필터 내부에서 응답 처리
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isExcludedUri(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

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

        throw new UnauthorizedException("토큰이 존재하지 않습니다.");
    }

    private void setPathMatcher() {
        List<String> excludedPaths = List.of(
                "/members/auth/login",
                "/admin/login", "/admin/signup",
                "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**", "/swagger-resources/**", "/webjars/**"
        );
        pathMatcher = new PathMatcherHelper(excludedPaths);
    }

    private boolean isExcludedUri(String uri) {
        return pathMatcher.matches(uri);
    }

    private void handleRefreshToken(String token, HttpServletResponse response) {
        if (jwtParser.isInvalid(token)) {
            throw new UnauthorizedException("유효하지 않은 refresh token입니다.");
        }

        String reissuedAccessToken = reissueAccessToken(token);
        response.setHeader("Authorization", "Bearer " + reissuedAccessToken);
    }

    private String reissueAccessToken(String token) {
        Long id = jwtParser.getIdFrom(token);
        Role role = jwtParser.getRoleFrom(token);
        return jwtProvider.createAccessToken(id, role, Instant.now());
    }

    private void handleAccessToken(String token) {
        if (jwtParser.isInvalid(token)) {
            throw new UnauthorizedException("유효하지 않은 access token입니다.");
        }

        Long id = jwtParser.getIdFrom(token);
        Role role = jwtParser.getRoleFrom(token);
        authContext.setAuthentication(id, role);
    }
}