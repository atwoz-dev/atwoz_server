package awtoz.awtoz.common.auth.config;


import awtoz.awtoz.common.auth.presentation.resolver.AuthResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class MemberAuthConfig implements WebMvcConfigurer {
    private final AuthResolver authResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(authResolver);
    }

}
