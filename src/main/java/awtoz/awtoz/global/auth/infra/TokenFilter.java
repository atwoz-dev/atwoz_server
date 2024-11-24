package awtoz.awtoz.global.auth.infra;


import awtoz.awtoz.global.auth.domain.TokenProvider;
import awtoz.awtoz.global.auth.presentation.support.AuthContext;
import awtoz.awtoz.global.auth.presentation.support.TokenExtractor;
import awtoz.awtoz.global.auth.infra.exception.TokenNotExistException;
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
    private final AuthContext authContext;
    private final TokenExceptionHandler tokenExceptionHandler;
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isExcluded(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = TokenExtractor.extractTokenFromRequest(request)
                    .orElseThrow(() -> new TokenNotExistException());

            Long memberId = tokenProvider.extract(token, "id", Long.class);
            authContext.setAuthentication(memberId);

        } catch (Exception e) {
            tokenExceptionHandler.handleException(response, e);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isExcluded(HttpServletRequest request) {
        return EXCLUDE_URLS.contains(request.getRequestURI());
    }
}
