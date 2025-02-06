package atwoz.atwoz.member.command.infra.member;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class MemberLockRepository {
    private static final TimeUnit timeUnit = TimeUnit.SECONDS;
    private static final Long waitTime = 5L;
    private static final Long leaseTime = 5L;

    private final RedissonClient redissonClient;

    public boolean getLockForLogin(String phoneNumber) {
        RLock lock = redissonClient.getLock("LOGIN_LOCK:" + phoneNumber);

        try {
            return lock.tryLock(waitTime, leaseTime, timeUnit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void releaseLockForLogin(String phoneNumber) {
        RLock lock = redissonClient.getLock("LOGIN_LOCK:" + phoneNumber);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
