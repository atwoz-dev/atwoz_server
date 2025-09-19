package atwoz.atwoz.payment.command.infra.order;

import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.payment.command.infra.order.event.AppStoreTokenExpiredEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppStoreTokenCacheManager 단위 테스트")
class AppStoreTokenCacheManagerTest {

    private static final String CACHE_KEY = "app_store:jwt_token";
    private static final String TOKEN = "test.jwt.token";
    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @InjectMocks
    private AppStoreTokenCacheManager cacheManager;


    @Nested
    @DisplayName("getCachedToken 메서드는")
    class GetCachedTokenTests {
        @DisplayName("캐시에서 토큰을 조회한다")
        @Test
        void getCachedToken_ReturnsTokenFromCache() {
            // given
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(CACHE_KEY)).thenReturn(TOKEN);

            // when
            String result = cacheManager.getCachedToken();

            // then
            assertThat(result).isEqualTo(TOKEN);
        }
    }

    @Nested
    @DisplayName("getCachedTokenWithSoftTtlCheck 메서드는")
    class GetCachedTokenWithSoftTtlCheckTests {
        @DisplayName("토큰이 없으면 null을 반환하고 이벤트를 발행하지 않는다")
        @Test
        void getCachedTokenWithSoftTtlCheck_WhenNoToken_ReturnsNullAndDoesNotPublishEvent() {
            // given
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(CACHE_KEY)).thenReturn(null);

            try (MockedStatic<Events> eventsMock = mockStatic(Events.class)) {
                // when
                String result = cacheManager.getCachedTokenWithSoftTtlCheck();

                // then
                assertThat(result).isNull();
                eventsMock.verifyNoInteractions();
            }
        }

        @DisplayName("토큰이 있고 soft TTL이 만료되었으면 이벤트를 발행한다")
        @Test
        void getCachedTokenWithSoftTtlCheck_WhenSoftTtlExpired_PublishesEvent() {
            // given
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(CACHE_KEY)).thenReturn(TOKEN);
            when(redisTemplate.getExpire(CACHE_KEY)).thenReturn(2000L); // soft TTL 만료 (3000초 미만)

            try (MockedStatic<Events> eventsMock = mockStatic(Events.class)) {
                // when
                String result = cacheManager.getCachedTokenWithSoftTtlCheck();

                // then
                assertThat(result).isEqualTo(TOKEN);
                eventsMock.verify(() -> Events.raise(any(AppStoreTokenExpiredEvent.class)));
            }
        }

        @DisplayName("토큰이 있지만 soft TTL이 만료되지 않았으면 이벤트를 발행하지 않는다")
        @Test
        void getCachedTokenWithSoftTtlCheck_WhenSoftTtlNotExpired_DoesNotPublishEvent() {
            // given
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(CACHE_KEY)).thenReturn(TOKEN);
            when(redisTemplate.getExpire(CACHE_KEY)).thenReturn(3500L); // soft TTL 미만료 (3000초 초과)

            try (MockedStatic<Events> eventsMock = mockStatic(Events.class)) {
                // when
                String result = cacheManager.getCachedTokenWithSoftTtlCheck();

                // then
                assertThat(result).isEqualTo(TOKEN);
                eventsMock.verifyNoInteractions();
            }
        }
    }

    @Nested
    @DisplayName("hasCachedToken 메서드는")
    class HasCachedTokenTests {
        @DisplayName("토큰이 null이면 캐시된 토큰이 없다고 판단한다")
        @Test
        void hasCachedToken_WhenTokenIsNull_ReturnsFalse() {
            // when
            boolean result = cacheManager.hasCachedToken(null);

            // then
            assertThat(result).isFalse();
        }

        @DisplayName("토큰이 있으면 캐시된 토큰이 있다고 판단한다")
        @Test
        void hasCachedToken_WhenTokenExists_ReturnsTrue() {
            // when
            boolean result = cacheManager.hasCachedToken(TOKEN);

            // then
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("cacheToken 메서드는")
    class CacheTokenTests {
        @DisplayName("토큰을 캐시에 저장한다")
        @Test
        void cacheToken_StoresTokenInCache() {
            // given
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);

            // when
            cacheManager.cacheToken(TOKEN);

            // then
            verify(valueOperations).set(eq(CACHE_KEY), eq(TOKEN), eq(Duration.ofSeconds(3600)));
        }
    }

    @Nested
    @DisplayName("isSoftTtlExpired 메서드는")
    class IsSoftTtlExpiredTests {
        @DisplayName("TTL이 soft TTL보다 작거나 같으면 만료되었다고 판단한다")
        @Test
        void isSoftTtlExpired_WhenTtlLessOrEqualToSoftTtl_ReturnsTrue() {
            // given
            when(redisTemplate.getExpire(CACHE_KEY)).thenReturn(3000L); // soft TTL과 같음

            // when
            boolean result = cacheManager.isSoftTtlExpired();

            // then
            assertThat(result).isTrue();
        }

        @DisplayName("TTL이 soft TTL보다 크면 만료되지 않았다고 판단한다")
        @Test
        void isSoftTtlExpired_WhenTtlGreaterThanSoftTtl_ReturnsFalse() {
            // given
            when(redisTemplate.getExpire(CACHE_KEY)).thenReturn(3500L); // soft TTL보다 큼

            // when
            boolean result = cacheManager.isSoftTtlExpired();

            // then
            assertThat(result).isFalse();
        }

        @DisplayName("TTL이 null이면 키가 존재하지 않으므로 만료되었다고 판단한다")
        @Test
        void isSoftTtlExpired_WhenTtlIsNull_ReturnsTrue() {
            // given
            when(redisTemplate.getExpire(CACHE_KEY)).thenReturn(null);

            // when
            boolean result = cacheManager.isSoftTtlExpired();

            // then
            assertThat(result).isTrue();
        }
    }
}