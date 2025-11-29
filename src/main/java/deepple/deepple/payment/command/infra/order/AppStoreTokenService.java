package deepple.deepple.payment.command.infra.order;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AppStoreTokenService {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AppStoreJwtTokenBuilder jwtTokenBuilder;
    private final AppStoreTokenCacheManager cacheManager;
    private final AppStoreTokenLockManager lockManager;

    public String generateToken() {
        String cachedToken = cacheManager.getCachedTokenWithSoftTtlCheck();
        if (cacheManager.hasCachedToken(cachedToken)) {
            return addBearerPrefix(cachedToken);
        }

        String token = lockManager.executeWithLock(this::generateTokenIfNotCached);
        return addBearerPrefix(token);
    }

    public String forceRefreshToken() {
        String token = generateAndCacheToken();
        return addBearerPrefix(token);
    }

    public String refreshToken() {
        String token = lockManager.executeWithLock(this::generateTokenIfSoftTtlExpired);
        return addBearerPrefix(token);
    }

    private String addBearerPrefix(String token) {
        return BEARER_PREFIX + token;
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
