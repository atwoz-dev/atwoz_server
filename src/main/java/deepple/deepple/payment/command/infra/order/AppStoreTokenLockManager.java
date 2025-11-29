package deepple.deepple.payment.command.infra.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppStoreTokenLockManager {

    private static final String TOKEN_CREATION_LOCK_KEY = "app_store:jwt_token:creating";
    private static final int LOCK_WAIT_TIME_SECONDS = 1;
    private static final int LOCK_LEASE_TIME_SECONDS = 2;

    private final RedissonClient redissonClient;

    public String executeWithLock(Supplier<String> operation) {
        RLock lock = acquireTokenCreationLock();
        try {
            return operation.get();
        } finally {
            releaseTokenCreationLockSafely(lock);
        }
    }

    private RLock acquireTokenCreationLock() {
        RLock lock = redissonClient.getLock(TOKEN_CREATION_LOCK_KEY);
        try {
            boolean isLockAcquired = lock.tryLock(LOCK_WAIT_TIME_SECONDS, LOCK_LEASE_TIME_SECONDS, TimeUnit.SECONDS);
            if (!isLockAcquired) {
                throw new RuntimeException("토큰 생성 락 획득 실패 - 다른 프로세스가 토큰을 생성 중입니다");
            }
            return lock;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("토큰 생성 락 대기 중 인터럽트 발생", exception);
        }
    }

    private void releaseTokenCreationLockSafely(RLock lock) {
        if (lock.isHeldByCurrentThread()) {
            try {
                lock.unlock();
                log.debug("App Store 토큰 생성 락 해제 완료");
            } catch (Exception exception) {
                log.error("App Store 토큰 생성 락 해제 실패 - lease time에 의해 자동 해제됨 ({}초)",
                    LOCK_LEASE_TIME_SECONDS, exception);
            }
        }
    }
}
