package deepple.deepple.common.repository;

import deepple.deepple.common.exception.CannotGetLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@Slf4j
@RequiredArgsConstructor
public class RedissonLockRepository {
    private final static String PREFIX = "REDISSON:";
    private final RedissonClient redissonClient;

    public void withLock(Runnable runnable, String key, int waitTime, int leaseTime) {
        RLock lock = redissonClient.getLock(PREFIX + key);
        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
            if (!isLocked) {
                log.error("Lock 획득 실패 = {}", key);
                return;
            }
            runnable.run();
        } catch (Exception e) {
            log.error("잠금 에러 발생 = {}", key, e);
            throw new CannotGetLockException(e);
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
