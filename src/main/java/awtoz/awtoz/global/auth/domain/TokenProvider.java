package awtoz.awtoz.global.auth.domain;

import org.springframework.stereotype.Component;

@Component
public interface TokenProvider {
    String createAccessToken(Long id);
    String createRefreshToken(Long id);
    <T> T extract(String token, String claimName, Class<T> classType);
}
