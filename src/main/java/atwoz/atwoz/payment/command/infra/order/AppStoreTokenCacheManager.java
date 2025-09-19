package atwoz.atwoz.payment.command.infra.order;

import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.payment.command.infra.order.event.AppStoreTokenExpiredEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AppStoreTokenCacheManager {
    private static final String APP_STORE_JWT_TOKEN_CACHE_KEY = "app_store:jwt_token";
    private static final String APP_STORE_TOKEN_EVENT_LOCK_KEY = "app_store:jwt_token:event_published";
    private static final long TOKEN_VALIDITY_SECONDS = 3600; // 1시간 (hard TTL)
    private static final long TOKEN_SOFT_TTL_SECONDS = 3000; // 50분 (soft TTL)
    private static final long EVENT_PUBLISH_INTERVAL_SECONDS = 10; // 이벤트 발행 간격

    private final RedisTemplate<String, String> redisTemplate;

    public String getCachedToken() {
        return redisTemplate.opsForValue().get(APP_STORE_JWT_TOKEN_CACHE_KEY);
    }

    public String getCachedTokenWithSoftTtlCheck() {
        String cachedToken = getCachedToken();
        if (hasCachedToken(cachedToken) && isSoftTtlExpired()) {
            checkAndPublishTokenExpiredEventIfNeeded();
        }
        return cachedToken;
    }

    public boolean hasCachedToken(String token) {
        return token != null;
    }

    public void cacheToken(String token) {
        Duration tokenExpiration = Duration.ofSeconds(TOKEN_VALIDITY_SECONDS);
        redisTemplate.opsForValue().set(APP_STORE_JWT_TOKEN_CACHE_KEY, token, tokenExpiration);
        log.info("App Store JWT 토큰 캐시 저장 완료");
    }

    public boolean isSoftTtlExpired() {
        Long currentTtl = redisTemplate.getExpire(APP_STORE_JWT_TOKEN_CACHE_KEY);
        return currentTtl == null || currentTtl <= TOKEN_SOFT_TTL_SECONDS;
    }

    private void checkAndPublishTokenExpiredEventIfNeeded() {
        Long currentTtl = redisTemplate.getExpire(APP_STORE_JWT_TOKEN_CACHE_KEY);

        if (isTokenRefreshNotNeeded(currentTtl)) {
            return;
        }

        publishTokenExpiredEvent(currentTtl);
    }

    private boolean isTokenRefreshNotNeeded(Long currentTtl) {
        return currentTtl != null && currentTtl > TOKEN_SOFT_TTL_SECONDS;
    }

    private void publishTokenExpiredEvent(Long currentTtl) {
        Duration eventLockDuration = Duration.ofSeconds(EVENT_PUBLISH_INTERVAL_SECONDS);
        Boolean lockAcquired = redisTemplate.opsForValue()
            .setIfAbsent(APP_STORE_TOKEN_EVENT_LOCK_KEY, "published", eventLockDuration);

        if (Boolean.FALSE.equals(lockAcquired)) {
            log.debug("토큰 갱신 이벤트 발행 제한 - {}초 이내에 이미 발행됨", EVENT_PUBLISH_INTERVAL_SECONDS);
            return;
        }

        log.debug("토큰 soft TTL 만료 감지, 현재 TTL: {}초", currentTtl);
        log.info("App Store JWT 토큰 soft TTL 만료, 갱신 이벤트 발행");
        Events.raise(new AppStoreTokenExpiredEvent(APP_STORE_JWT_TOKEN_CACHE_KEY));
    }

}