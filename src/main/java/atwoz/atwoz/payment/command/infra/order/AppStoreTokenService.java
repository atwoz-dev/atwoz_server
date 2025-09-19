package atwoz.atwoz.payment.command.infra.order;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AppStoreTokenService {

    private final AppStoreJwtTokenBuilder jwtTokenBuilder;
    private final AppStoreTokenCacheManager cacheManager;
    private final AppStoreTokenLockManager lockManager;

    public String generateToken() {
        String cachedToken = cacheManager.getCachedTokenWithSoftTtlCheck();
        if (cacheManager.hasCachedToken(cachedToken)) {
            return cachedToken;
        }

        return lockManager.executeWithLock(this::generateTokenIfNotCached);
    }

    public String forceRefreshToken() {
        return generateAndCacheToken();
    }

    public String refreshToken() {
        return lockManager.executeWithLock(this::generateTokenIfSoftTtlExpired);
    }

    private String generateTokenIfNotCached() {
        String existingToken = cacheManager.getCachedToken();
        if (cacheManager.hasCachedToken(existingToken)) {
            return existingToken;
        }
        return generateAndCacheToken();
    }

    private String generateTokenIfSoftTtlExpired() {
        String existingToken = cacheManager.getCachedToken();
        if (cacheManager.hasCachedToken(existingToken) && !cacheManager.isSoftTtlExpired()) {
            return existingToken;
        }
        return generateAndCacheToken();
    }

    private String generateAndCacheToken() {
        String token = jwtTokenBuilder.buildToken();
        cacheManager.cacheToken(token);
        log.info("새로운 App Store JWT 토큰 생성 완료");
        return token;
    }
}