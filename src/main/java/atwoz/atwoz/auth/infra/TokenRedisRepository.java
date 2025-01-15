package atwoz.atwoz.auth.infra;

import atwoz.atwoz.auth.domain.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TokenRedisRepository implements TokenRepository {

    private static final String PREFIX = "jwt:whitelist:";

    private final RedisTemplate<String, String> redisTemplate;

    public void save(String token) {
        redisTemplate.opsForValue().set(PREFIX + token, "");
    }

    public void delete(String token) {
        redisTemplate.delete(PREFIX + token);
    }

    public boolean exists(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + token));
    }
}
