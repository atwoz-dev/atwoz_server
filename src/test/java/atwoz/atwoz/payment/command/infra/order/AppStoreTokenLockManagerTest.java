package atwoz.atwoz.payment.command.infra.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppStoreTokenLockManager 단위 테스트")
class AppStoreTokenLockManagerTest {

    private static final String OPERATION_RESULT = "operation_result";
    @Mock
    private RedissonClient redissonClient;
    @Mock
    private RLock lock;
    @InjectMocks
    private AppStoreTokenLockManager lockManager;

    @Nested
    @DisplayName("executeWithLock 메서드는")
    class ExecuteWithLockTests {
        @DisplayName("락 획득에 성공하면 작업을 실행하고 결과를 반환한다")
        @Test
        void whenLockAcquiredSuccessfully_executesOperationAndReturnsResult() throws InterruptedException {
            // given
            when(redissonClient.getLock("app_store:jwt_token:creating")).thenReturn(lock);
            when(lock.tryLock(1L, 2L, TimeUnit.SECONDS)).thenReturn(true);
            when(lock.isHeldByCurrentThread()).thenReturn(true);

            Supplier<String> operation = () -> OPERATION_RESULT;

            // when
            String result = lockManager.executeWithLock(operation);

            // then
            assertThat(result).isEqualTo(OPERATION_RESULT);
            verify(lock).tryLock(1, 2, TimeUnit.SECONDS);
            verify(lock).unlock();
        }

        @DisplayName("락 획득에 실패하면 RuntimeException을 던진다")
        @Test
        void whenLockAcquisitionFails_throwsRuntimeException() throws InterruptedException {
            // given
            when(redissonClient.getLock("app_store:jwt_token:creating")).thenReturn(lock);
            when(lock.tryLock(1, 2, TimeUnit.SECONDS)).thenReturn(false);

            Supplier<String> operation = () -> OPERATION_RESULT;

            // when & then
            assertThatThrownBy(() -> lockManager.executeWithLock(operation))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("토큰 생성 락 획득 실패 - 다른 프로세스가 토큰을 생성 중입니다");

            verify(lock).tryLock(1, 2, TimeUnit.SECONDS);
            verify(lock, never()).unlock();
        }

        @DisplayName("락 대기 중 인터럽트가 발생하면 RuntimeException을 던진다")
        @Test
        void whenInterruptedDuringLockWait_throwsRuntimeException() throws InterruptedException {
            // given
            when(redissonClient.getLock("app_store:jwt_token:creating")).thenReturn(lock);
            when(lock.tryLock(1, 2, TimeUnit.SECONDS))
                .thenThrow(new InterruptedException("Thread interrupted"));

            Supplier<String> operation = () -> OPERATION_RESULT;

            // when & then
            assertThatThrownBy(() -> lockManager.executeWithLock(operation))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("토큰 생성 락 대기 중 인터럽트 발생");

            verify(lock, never()).unlock();
        }

        @DisplayName("작업 실행 중 예외가 발생해도 락은 해제된다")
        @Test
        void whenOperationThrowsException_releasesLock() throws InterruptedException {
            // given
            when(redissonClient.getLock("app_store:jwt_token:creating")).thenReturn(lock);
            when(lock.tryLock(1L, 2L, TimeUnit.SECONDS)).thenReturn(true);
            when(lock.isHeldByCurrentThread()).thenReturn(true);

            Supplier<String> operation = () -> {
                throw new RuntimeException("Operation failed");
            };

            // when & then
            assertThatThrownBy(() -> lockManager.executeWithLock(operation))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Operation failed");

            verify(lock).unlock();
        }

        @DisplayName("현재 스레드가 락을 보유하지 않으면 unlock을 호출하지 않는다")
        @Test
        void whenLockNotHeldByCurrentThread_doesNotUnlock() throws InterruptedException {
            // given
            when(redissonClient.getLock("app_store:jwt_token:creating")).thenReturn(lock);
            when(lock.tryLock(1L, 2L, TimeUnit.SECONDS)).thenReturn(true);
            when(lock.isHeldByCurrentThread()).thenReturn(false);

            Supplier<String> operation = () -> OPERATION_RESULT;

            // when
            String result = lockManager.executeWithLock(operation);

            // then
            assertThat(result).isEqualTo(OPERATION_RESULT);
            verify(lock, never()).unlock();
        }

        @DisplayName("unlock 중 예외가 발생해도 무시한다")
        @Test
        void whenUnlockThrowsException_ignoresException() throws InterruptedException {
            // given
            when(redissonClient.getLock("app_store:jwt_token:creating")).thenReturn(lock);
            when(lock.tryLock(1L, 2L, TimeUnit.SECONDS)).thenReturn(true);
            when(lock.isHeldByCurrentThread()).thenReturn(true);
            doThrow(new RuntimeException("Unlock failed")).when(lock).unlock();

            Supplier<String> operation = () -> OPERATION_RESULT;

            // when
            String result = lockManager.executeWithLock(operation);

            // then
            assertThat(result).isEqualTo(OPERATION_RESULT);
            verify(lock).unlock();
        }
    }
}
