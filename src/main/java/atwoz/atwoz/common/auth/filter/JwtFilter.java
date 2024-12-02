package atwoz.atwoz.common.auth.filter;


import atwoz.atwoz.common.auth.AccessTokenExtractor;
import atwoz.atwoz.common.auth.AuthContext;
import atwoz.atwoz.common.auth.Role;
import atwoz.atwoz.common.auth.exception.UnauthorizedException;
import atwoz.atwoz.common.auth.jwt.JwtParser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final Set<String> EXCLUDED_URIS = Set.of(
            // member login
            "/members/auth/login",

            // admin login
            "/admin/login",
            "/admin/signup",

            // swagger
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/webjars/**"
    );
    private static final String ADMIN_URI = "/admin";
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private final AuthContext authContext;
    private final TokenExceptionHandler tokenExceptionHandler;
    private final JwtParser jwtParser;

    private final AccessTokenExtractor accessTokenExtractor;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. 인증이 필요한 엔드포인트인지 확인
        String requestUri = request.getRequestURI();
        if (isExcluded(requestUri)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Refresh token 추출

        // 3. Access token 추출
        // TODO: MissingAuthorizationHeaderException, InvalidAuthorizationHeaderException 처리 필요
        try {
            String accessToken = accessTokenExtractor.extract(request);
            Long id = jwtParser.getIdFrom(accessToken);
            Role role = jwtParser.getRoleFrom(accessToken);

            if (isIncludedAdminURI(request) && role != Role.ADMIN)
                throw new UnauthorizedException();

            authContext.setAuthentication(id);
            filterChain.doFilter(request, response);
        } catch (RuntimeException e) {
            tokenExceptionHandler.handleException(response, e);
        }
    }

    private boolean isExcluded(String uri) {
        return EXCLUDED_URIS.stream().anyMatch(pattern -> PATH_MATCHER.match(pattern, uri));
    }

    private boolean isIncludedAdminURI(HttpServletRequest request) {
        return request.getRequestURI().startsWith(ADMIN_URI);
    }
}
