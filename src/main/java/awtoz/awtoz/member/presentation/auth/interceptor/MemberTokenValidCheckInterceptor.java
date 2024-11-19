package awtoz.awtoz.member.presentation.auth.interceptor;

import awtoz.awtoz.member.infra.auth.MemberJwtTokenProvider;
import awtoz.awtoz.member.presentation.auth.support.MemberAuthContext;
import awtoz.awtoz.member.presentation.auth.support.MemberTokenExtractor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class MemberTokenValidCheckInterceptor implements HandlerInterceptor {

    private final MemberJwtTokenProvider memberJwtTokenProvider;
    private final MemberAuthContext memberAuthContext;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = MemberTokenExtractor.extractTokenFromRequest(request)
                .orElseThrow(() -> new RuntimeException("토큰이 없습니다."));


        // TODO : 토큰에서 정보를 추출하여, 컨텍스트에 담기.
        Long extractedMemberId = memberJwtTokenProvider.extract(token, "id", Long.class);
        memberAuthContext.setAuthentication(extractedMemberId);

        return true;
    }
}
