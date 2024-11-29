package atwoz.atwoz.common.auth.infra;


import atwoz.atwoz.common.auth.domain.Role;
import atwoz.atwoz.common.auth.exception.UnauthorizedException;
import atwoz.atwoz.common.auth.infra.exception.TokenNotExistException;
import atwoz.atwoz.common.auth.presentation.support.AuthContext;
import atwoz.atwoz.common.auth.presentation.support.TokenExtractor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenFilter extends OncePerRequestFilter {

    private static final List<String> EXCLUDE_URLS = List.of(
            // login
            "/members/auth/login",
            // swagger
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/webjars/**"
    );
    private static final String ADMIN_URL = "/admin";
    private final AuthContext authContext;
    private final TokenExceptionHandler tokenExceptionHandler;
    private final JwtProvider jwtProvider;
    private final JwtParser jwtParser;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isExcluded(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = TokenExtractor.extractTokenFromRequest(request)
                    .orElseThrow(() -> new TokenNotExistException());

            Long memberId = jwtParser.getIdFrom(token);
            Role role = jwtParser.getRoleFrom(token);

            if (isIncludedAdminURI(request) && role != Role.ADMIN)
                throw new UnauthorizedException();

            authContext.setAuthentication(memberId);
            filterChain.doFilter(request, response);

        } catch (RuntimeException e) {
            tokenExceptionHandler.handleException(response, e);
        }
    }

    private boolean isExcluded(HttpServletRequest request) {

        return EXCLUDE_URLS.contains(request.getRequestURI());
    }

    private boolean isIncludedAdminURI(HttpServletRequest request) {
        return request.getRequestURI().startsWith(ADMIN_URL);
    }
}
