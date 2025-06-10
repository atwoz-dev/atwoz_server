package atwoz.atwoz.member.command.infra.member;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class AuthMessageRedisRepository {
    private static final String PREFIX = "authMessage:";
    private static final int EXPIRE_TIME_MINUTE = 5;

    private final RedisTemplate<String, String> redisTemplate;

    public void save(String key, String message) {
        String fullKey = PREFIX + key;
        redisTemplate.opsForValue().set(fullKey, message);
        redisTemplate.expire(fullKey, EXPIRE_TIME_MINUTE, TimeUnit.MINUTES);
    }

    public void delete(String key) {
        String fullKey = PREFIX + key;
        redisTemplate.delete(fullKey);
    }
}
