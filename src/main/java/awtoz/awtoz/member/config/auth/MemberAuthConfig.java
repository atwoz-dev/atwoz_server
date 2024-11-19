package awtoz.awtoz.member.config.auth;

import awtoz.awtoz.member.presentation.auth.interceptor.MemberTokenValidCheckInterceptor;
import awtoz.awtoz.member.presentation.auth.resolver.MemberAuthResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class MemberAuthConfig implements WebMvcConfigurer {
    private final MemberAuthResolver memberAuthResolver;
    private final MemberTokenValidCheckInterceptor memberTokenValidCheckInterceptor;

    /**
     * 인증/인가가 필요한 URI 명시.
     *
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(memberTokenValidCheckInterceptor)
                .excludePathPatterns("/api/members/auth/**", HttpMethod.POST.name())
                .addPathPatterns("/api/members/**", HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(), HttpMethod.DELETE.name())
                .addPathPatterns("/api/members", "GET");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(memberAuthResolver);
    }

}
