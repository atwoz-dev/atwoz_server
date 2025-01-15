package atwoz.atwoz.auth.presentation.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PathMatcherConfig {

    private static final List<String> EXCLUDED_URIS = List.of(
            "/member/auth/login", "/member/auth/logout",
            "/admin/login", "/admin/signup",
            "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**", "/swagger-resources/**", "/webjars/**"
    );

    @Bean
    public PathMatcherHelper pathMatcherHelper() {
        return new PathMatcherHelper(EXCLUDED_URIS);
    }
}
