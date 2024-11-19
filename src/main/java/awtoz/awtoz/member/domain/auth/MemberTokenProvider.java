package awtoz.awtoz.member.domain.auth;

import org.springframework.stereotype.Component;

@Component
public interface MemberTokenProvider {
    String createAccessToken(Long id);
    String createRefreshToken(Long id);
    <T> T extract(String token, String claimName, Class<T> classType);
}
