package atwoz.atwoz.auth.infra;

import atwoz.atwoz.auth.domain.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class TokenRedisRepository implements TokenRepository {

    private static final String KEY_PATTERN = "jwt::whitelist::%s";
    private static final Duration TTL = Duration.ofDays(14);

    private final RedisTemplate<String, String> redisTemplate;

    public void save(String token) {
        redisTemplate.opsForValue().set(generateKey(token), "", TTL);
    }

    public void delete(String token) {
        redisTemplate.delete(generateKey(token));
    }

    public boolean exists(String token) {
        return redisTemplate.hasKey(generateKey(token));
    }

    private String generateKey(String token) {
        return String.format(KEY_PATTERN, token);
    }
}
