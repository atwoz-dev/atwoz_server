package atwoz.atwoz.auth.presentation;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "auth.refresh-token-cookie")
@Getter
@Setter
public class RefreshTokenCookieProperties {

    private String name;
    private int maxAge;
    private String path;
    private boolean secure;
    private boolean httpOnly;
}
