package atwoz.atwoz.common.auth.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class JwtRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public void save(String token) {
        redisTemplate.opsForValue().set(token, "", 1, TimeUnit.DAYS);
    }

    public boolean isExists(String token) {
        return redisTemplate.hasKey(token);
    }
}
