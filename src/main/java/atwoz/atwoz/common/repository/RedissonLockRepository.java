package atwoz.atwoz.common.repository;

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
        try {
            boolean lockable = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
            if (!lockable) {
                log.error("Lock 획득 실패 = {}", key);
                return;
            }
            runnable.run();
        } catch (Exception e) {
            log.error("잠금 에러 발생 = {}", key, e);
        } finally {
            lock.unlock();
        }
    }
}
