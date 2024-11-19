package awtoz.awtoz.member.presentation.auth.resolver;

import awtoz.awtoz.member.presentation.auth.support.AuthMember;
import awtoz.awtoz.member.presentation.auth.support.MemberAuthContext;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class MemberAuthResolver implements HandlerMethodArgumentResolver {
    private final MemberAuthContext memberAuthContext;


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return memberAuthContext.getPrincipal();
    }
}
