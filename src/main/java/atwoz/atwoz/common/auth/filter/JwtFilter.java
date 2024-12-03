package atwoz.atwoz.common.auth.filter;


import atwoz.atwoz.common.auth.AccessTokenExtractor;
import atwoz.atwoz.common.auth.AuthContext;
import atwoz.atwoz.common.auth.RefreshTokenExtractor;
import atwoz.atwoz.common.auth.Role;
import atwoz.atwoz.common.auth.exception.UnauthorizedException;
import atwoz.atwoz.common.auth.jwt.JwtParser;
import atwoz.atwoz.common.auth.jwt.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final Set<String> EXCLUDED_URIS = Set.of(
            "/members/auth/login",
            "/admin/login", "/admin/signup",
            "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**", "/swagger-resources/**", "/webjars/**"
    );

    private final JwtProvider jwtProvider;
    private final JwtParser jwtParser;

    private final AuthContext authContext;
    private final TokenExceptionHandler tokenExceptionHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // URI 체크
        if (isExcluded(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // TODO: error handling

        // Refresh token 처리
        Optional<String> refreshToken = RefreshTokenExtractor.extractFrom(request);
        if (refreshToken.isPresent()) {
            String reissuedAccessToken = handleRefreshToken(refreshToken.get());
            // 재발급 받은 access token을 응답으로 내려줌
            return;
        }

        // Access token 처리
        Optional<String> accessToken = AccessTokenExtractor.extractFrom(request);
        if (accessToken.isPresent()) {
            handleAccessToken(accessToken.get());
            filterChain.doFilter(request, response);
        }

        throw new UnauthorizedException("Access token이 존재하지 않습니다.");
    }

    private boolean isExcluded(String uri) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return EXCLUDED_URIS.stream().anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }

    private String handleRefreshToken(String token) {
        if (jwtParser.isInvalid(token)) {
            throw new UnauthorizedException("유효하지 않은 refresh token입니다.");
        }

        // Access token 재발급
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

        authContext.setAuthentication(id, role);  // todo: authContext 수정
    }
}