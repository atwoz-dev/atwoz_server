package atwoz.atwoz.auth.presentation.filter;


import atwoz.atwoz.auth.application.AuthResult;
import atwoz.atwoz.auth.application.AuthService;
import atwoz.atwoz.auth.domain.Role;
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
import java.util.Optional;

import static atwoz.atwoz.common.StatusType.INVALID_ACCESS_TOKEN;
import static atwoz.atwoz.common.StatusType.MISSING_ACCESS_TOKEN;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {

    private static final int FOUR_WEEKS_IN_SECONDS = 60 * 60 * 24 * 7 * 4;

    private final PathMatcherHelper pathMatcherHelper;
    private final TokenExtractor tokenExtractor;
    private final AuthService authService;
    private final ResponseHandler responseHandler;
    private final AuthContext authContext;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isExcluded(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // TODO: status type, role 어떻게 다룰지 수정 필요

        Optional<String> optionalAccessToken = tokenExtractor.extractAccessToken(request);
        Optional<String> optionalRefreshToken = tokenExtractor.extractRefreshToken(request);

        if (optionalAccessToken.isEmpty()) {
            setUnauthorizedResponse(response, MISSING_ACCESS_TOKEN);
            return;
        }

        String accessToken = optionalAccessToken.get();
        String refreshToken = optionalRefreshToken.orElse(null);

        AuthResult result = authService.authenticate(accessToken, refreshToken);

        if (!result.isSuccess()) {
            setUnauthorizedResponse(response, StatusType.valueOf(result.getErrorCode()));
            return;
        }

        Long memberId = result.getMemberId();
        String role = result.getRole();

        if (memberId != null && role != null) {
            authContext.authenticate(memberId, Role.valueOf(role));
        }

        if (result.isReissued()) {
            addAccessTokenToHeader(response, result.getReissuedAccessToken());
            addRefreshTokenToCookie(response, result.getReissuedRefreshToken());
        }

        // TODO: 응답 어떻게 할지 여기까지 도달하면 어떤 경우?
        setUnauthorizedResponse(response, INVALID_ACCESS_TOKEN);
    }

    private boolean isExcluded(String uri) {
        return pathMatcherHelper.isExcluded(uri);
    }

    private void addAccessTokenToHeader(HttpServletResponse response, String accessToken) {
        response.setHeader("Authorization", "Bearer " + accessToken);
    }

    private void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setMaxAge(FOUR_WEEKS_IN_SECONDS);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);

        response.addCookie(cookie);
    }

    private void setUnauthorizedResponse(HttpServletResponse response, StatusType statusType) {
        log.error("토큰 인증에 실패했습니다: {}", statusType.getMessage());
        responseHandler.setResponse(response, statusType);
    }
}