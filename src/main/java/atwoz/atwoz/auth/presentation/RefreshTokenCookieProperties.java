package atwoz.atwoz.auth.presentation;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.refresh-token-cookie")
public record RefreshTokenCookieProperties(
    String name,
    int maxAge,
    String path,
    String sameSite,
    boolean secure,
    boolean httpOnly
) {
}
