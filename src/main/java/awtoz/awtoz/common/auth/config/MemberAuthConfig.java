package awtoz.awtoz.common.auth.config;

import awtoz.awtoz.member.presentation.auth.interceptor.MemberTokenValidCheckInterceptor;
import awtoz.awtoz.common.auth.presentation.resolver.AuthResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class MemberAuthConfig implements WebMvcConfigurer {
    private final AuthResolver authResolver;
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
        argumentResolvers.add(authResolver);
    }

}
