package awtoz.awtoz.member.presentation.auth.interceptor;

import awtoz.awtoz.global.auth.infra.exception.TokenNotExistException;
import awtoz.awtoz.global.auth.infra.JwtTokenProvider;
import awtoz.awtoz.global.auth.presentation.support.AuthContext;
import awtoz.awtoz.global.auth.presentation.support.TokenExtractor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class MemberTokenValidCheckInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider memberJwtTokenProvider;
    private final AuthContext memberAuthContext;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = TokenExtractor.extractTokenFromRequest(request)
                .orElseThrow(() -> new TokenNotExistException());

        // TODO : 토큰에서 정보를 추출하여, 컨텍스트에 담기.
        Long extractedMemberId = memberJwtTokenProvider.extract(token, "id", Long.class);
        memberAuthContext.setAuthentication(extractedMemberId);

        return true;
    }
}
