package atwoz.atwoz.auth.infra.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class JwtRepository {

    private static final String JWT_WHITELIST_PREFIX = "jwt:whitelist:";

    private final RedisTemplate<String, String> redisTemplate;

    public void save(String token, Duration duration) {
        redisTemplate.opsForValue().set(JWT_WHITELIST_PREFIX + token, "", duration);
    }

    public void delete(String token) {
        redisTemplate.delete(JWT_WHITELIST_PREFIX + token);
    }

    public boolean isExists(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(JWT_WHITELIST_PREFIX + token));
    }
}
