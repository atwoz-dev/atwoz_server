package atwoz.atwoz.member.query.introduction.intra;

import atwoz.atwoz.member.query.introduction.intra.exception.IntroductionMemberIdSerializationFailedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class IntroductionRedisRepository {
    private static final String PREFIX = "introduction:";

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    public void saveIntroductionMemberIds(String key, Set<Long> introductionMemberIds, Date expireAt) {
        String fullKey = buildKey(key);
        try {
            String json = objectMapper.writeValueAsString(introductionMemberIds);
            redisTemplate.opsForValue().set(fullKey, json);
            redisTemplate.expireAt(fullKey, expireAt);
        } catch (JsonProcessingException e) {
            throw new IntroductionMemberIdSerializationFailedException(e);
        }
    }

    public Set<Long> findIntroductionMemberIds(String key) {
        String fullKey = buildKey(key);
        try {
            String json = redisTemplate.opsForValue().get(fullKey);
            if (json == null) {
                return Set.of();
            }
            return objectMapper.readValue(json, new TypeReference<Set<Long>>() {});
        } catch (JsonProcessingException e) {
            throw new IntroductionMemberIdSerializationFailedException(e);
        }
    }

    private String buildKey(String key) {
        return PREFIX + key;
    }
}
