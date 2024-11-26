package awtoz.awtoz.common.auth.infra;


import awtoz.awtoz.common.auth.domain.Role;
import awtoz.awtoz.common.auth.exception.UnauthorizedException;
import awtoz.awtoz.common.auth.infra.exception.TokenNotExistException;
import awtoz.awtoz.common.auth.presentation.support.AuthContext;
import awtoz.awtoz.common.auth.presentation.support.TokenExtractor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {

    private static final List<String> EXCLUDE_URLS = List.of("/member/auth/login");
    private static final String ADMIN_URL = "/admin";
    private final AuthContext authContext;
    private final TokenExceptionHandler tokenExceptionHandler;
    private final JwtProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isExcluded(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = TokenExtractor.extractTokenFromRequest(request)
                    .orElseThrow(() -> new TokenNotExistException());

            Long memberId = tokenProvider.extractId(token);
            Role role = tokenProvider.extractRole(token);

            if (isIncludedAdminURI(request) && role != Role.ADMIN)
                throw new UnauthorizedException();

            authContext.setAuthentication(memberId);
        } catch (RuntimeException e) {
            tokenExceptionHandler.handleException(response, e);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isExcluded(HttpServletRequest request) {
        return EXCLUDE_URLS.contains(request.getRequestURI());
    }

    private boolean isIncludedAdminURI(HttpServletRequest request) {
        return request.getRequestURI().startsWith(ADMIN_URL);
    }
}
