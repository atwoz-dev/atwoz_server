package deepple.deepple.payment.command.infra.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppStoreTokenService 단위 테스트")
class AppStoreTokenServiceTest {

    private static final String EXISTING_TOKEN = "existing.jwt.token";
    private static final String NEW_TOKEN = "new.jwt.token";
    private static final String BEARER_EXISTING_TOKEN = "Bearer " + EXISTING_TOKEN;
    private static final String BEARER_NEW_TOKEN = "Bearer " + NEW_TOKEN;

    @Mock
    private AppStoreJwtTokenBuilder jwtTokenBuilder;

    @Mock
    private AppStoreTokenCacheManager cacheManager;

    @Mock
    private AppStoreTokenLockManager lockManager;

    @InjectMocks
    private AppStoreTokenService tokenService;

    @Nested
    @DisplayName("토큰 생성 메서드는")
    class GenerateTokenTests {
        @DisplayName("캐시된 토큰이 있고 soft TTL이 만료되지 않았으면 기존 토큰을 반환한다")
        @Test
        void whenCachedTokenExistsAndNotExpired_returnsExistingToken() {
            // given
            when(cacheManager.getCachedTokenWithSoftTtlCheck()).thenReturn(EXISTING_TOKEN);
            when(cacheManager.hasCachedToken(EXISTING_TOKEN)).thenReturn(true);

            // when
            String result = tokenService.generateToken();

            // then
            assertThat(result).isEqualTo(BEARER_EXISTING_TOKEN);
            verify(lockManager, never()).executeWithLock(any());
            verify(jwtTokenBuilder, never()).buildToken();
        }

        @DisplayName("캐시된 토큰이 없으면 락을 획득하여 새 토큰을 생성한다")
        @Test
        void whenNoCachedToken_createsNewTokenWithLock() {
            // given
            when(cacheManager.getCachedTokenWithSoftTtlCheck()).thenReturn(null);
            when(cacheManager.hasCachedToken(null)).thenReturn(false);
            when(lockManager.executeWithLock(any())).thenAnswer(invocation -> {
                Supplier<String> supplier = invocation.getArgument(0);
                return supplier.get();
            });
            when(cacheManager.getCachedToken()).thenReturn(null);
            when(cacheManager.hasCachedToken(null)).thenReturn(false);
            when(jwtTokenBuilder.buildToken()).thenReturn(NEW_TOKEN);

            // when
            String result = tokenService.generateToken();

            // then
            assertThat(result).isEqualTo(BEARER_NEW_TOKEN);
            verify(lockManager, times(1)).executeWithLock(any());
            verify(jwtTokenBuilder, times(1)).buildToken();
            verify(cacheManager, times(1)).cacheToken(NEW_TOKEN);
        }

        @DisplayName("락 획득 후 다른 스레드가 토큰을 생성했다면 기존 토큰을 반환한다")
        @Test
        void whenTokenCreatedByOtherThreadAfterLockAcquisition_returnsExistingToken() {
            // given
            when(cacheManager.getCachedTokenWithSoftTtlCheck()).thenReturn(null);
            when(cacheManager.hasCachedToken(null)).thenReturn(false);
            when(lockManager.executeWithLock(any())).thenAnswer(invocation -> {
                Supplier<String> supplier = invocation.getArgument(0);
                return supplier.get();
            });
            // 락 획득 후에는 다른 스레드가 토큰을 생성했다고 가정
            when(cacheManager.getCachedToken()).thenReturn(EXISTING_TOKEN);
            when(cacheManager.hasCachedToken(EXISTING_TOKEN)).thenReturn(true);

            // when
            String result = tokenService.generateToken();

            // then
            assertThat(result).isEqualTo(BEARER_EXISTING_TOKEN);
            verify(lockManager, times(1)).executeWithLock(any());
            verify(jwtTokenBuilder, never()).buildToken();
            verify(cacheManager, never()).cacheToken(anyString());
        }
    }


    @Nested
    @DisplayName("강제 토큰 리프레시 메서드는")
    class ForceRefreshTokenTests {
        @DisplayName("락 없이 즉시 새 토큰을 생성한다")
        @Test
        void createsNewTokenWithoutLock() {
            // given
            when(jwtTokenBuilder.buildToken()).thenReturn(NEW_TOKEN);

            // when
            String result = tokenService.forceRefreshToken();

            // then
            assertThat(result).isEqualTo(BEARER_NEW_TOKEN);
            verify(lockManager, never()).executeWithLock(any());
            verify(jwtTokenBuilder, times(1)).buildToken();
            verify(cacheManager, times(1)).cacheToken(NEW_TOKEN);
        }
    }

    @Nested
    @DisplayName("토큰 리프레시 메서드는")
    class RefreshTokenTests {
        @DisplayName("토큰이 있고 만료되지 않았다면 기존 토큰을 반환한다")
        @Test
        void whenTokenExistsAndNotExpired_returnsExistingToken() {
            // given
            when(lockManager.executeWithLock(any())).thenAnswer(invocation -> {
                Supplier<String> supplier = invocation.getArgument(0);
                return supplier.get();
            });
            when(cacheManager.getCachedToken()).thenReturn(EXISTING_TOKEN);
            when(cacheManager.hasCachedToken(EXISTING_TOKEN)).thenReturn(true);
            when(cacheManager.isSoftTtlExpired()).thenReturn(false);

            // when
            String result = tokenService.refreshToken();

            // then
            assertThat(result).isEqualTo(BEARER_EXISTING_TOKEN);
            verify(lockManager, times(1)).executeWithLock(any());
            verify(jwtTokenBuilder, never()).buildToken();
            verify(cacheManager, never()).cacheToken(anyString());
        }

        @DisplayName("토큰이 없거나 만료되었다면 새 토큰을 생성한다")
        @Test
        void whenTokenExpired_createsNewToken() {
            // given
            when(lockManager.executeWithLock(any())).thenAnswer(invocation -> {
                Supplier<String> supplier = invocation.getArgument(0);
                return supplier.get();
            });
            when(cacheManager.getCachedToken()).thenReturn(EXISTING_TOKEN);
            when(cacheManager.hasCachedToken(EXISTING_TOKEN)).thenReturn(true);
            when(cacheManager.isSoftTtlExpired()).thenReturn(true);
            when(jwtTokenBuilder.buildToken()).thenReturn(NEW_TOKEN);

            // when
            String result = tokenService.refreshToken();

            // then
            assertThat(result).isEqualTo(BEARER_NEW_TOKEN);
            verify(lockManager, times(1)).executeWithLock(any());
            verify(jwtTokenBuilder, times(1)).buildToken();
            verify(cacheManager, times(1)).cacheToken(NEW_TOKEN);
        }

        @DisplayName("토큰이 없다면 새 토큰을 생성한다")
        @Test
        void whenNoToken_createsNewToken() {
            // given
            when(lockManager.executeWithLock(any())).thenAnswer(invocation -> {
                Supplier<String> supplier = invocation.getArgument(0);
                return supplier.get();
            });
            when(cacheManager.getCachedToken()).thenReturn(null);
            when(cacheManager.hasCachedToken(null)).thenReturn(false);
            when(jwtTokenBuilder.buildToken()).thenReturn(NEW_TOKEN);

            // when
            String result = tokenService.refreshToken();

            // then
            assertThat(result).isEqualTo(BEARER_NEW_TOKEN);
            verify(lockManager, times(1)).executeWithLock(any());
            verify(jwtTokenBuilder, times(1)).buildToken();
            verify(cacheManager, times(1)).cacheToken(NEW_TOKEN);
        }
    }
}
