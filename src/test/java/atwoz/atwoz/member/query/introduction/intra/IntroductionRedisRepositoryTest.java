package atwoz.atwoz.member.query.introduction.intra;

import atwoz.atwoz.member.query.introduction.intra.exception.IntroductionMemberIdSerializationFailedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Date;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IntroductionRedisRepositoryTest {

    @InjectMocks
    private IntroductionRedisRepository repository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOps;

    @Mock
    private ObjectMapper mapper = new ObjectMapper();

    @Nested
    @DisplayName("saveIntroductionMemberIds 메서드 테스트")
    class SaveIntroductionMemberIdsTest {
        @Test
        @DisplayName("Redis에 올바르게 저장되고 만료 시간 설정")
        void saveIntroductionMemberIdWhenSuccess() throws JsonProcessingException {
            // given
            String key = "testKey";
            Set<Long> ids = Set.of(1L, 2L, 3L);
            Date expireAt = new Date();
            String json = "[1,2,3]";

            when(mapper.writeValueAsString(ids)).thenReturn(json);
            when(redisTemplate.opsForValue()).thenReturn(valueOps);

            // when
            repository.saveIntroductionMemberIds(key, ids, expireAt);

            // then
            String fullKey = "introduction:" + key;
            verify(valueOps).set(eq(fullKey), eq(json));
            verify(redisTemplate).expireAt(eq(fullKey), eq(expireAt));
        }

        @Test
        @DisplayName("JSON 직렬화 실패 시 예외 발생")
        void throwExceptionWhenSerializationFailed() throws JsonProcessingException {
            // given
            String key = "testKey";
            Set<Long> ids = Set.of(1L, 2L, 3L);
            Date expireAt = new Date();

            when(mapper.writeValueAsString(ids)).thenThrow(new JsonProcessingException("error") {});

            // when & then
            assertThatThrownBy(() -> repository.saveIntroductionMemberIds(key, ids, expireAt))
                    .isInstanceOf(IntroductionMemberIdSerializationFailedException.class);
        }
    }

    @Nested
    @DisplayName("findIntroductionMemberIds 메서드 테스트")
    class FindIntroductionMemberIdsTest {
        @Test
        @DisplayName("Redis에 값이 없으면 빈 집합 반환")
        void returnEmptySetWhenRedisHasNoValue() {
            // given
            String key = "testKey";
            String fullKey = "introduction:" + key;

            when(redisTemplate.opsForValue()).thenReturn(valueOps);
            when(valueOps.get(fullKey)).thenReturn(null);

            // when
            Set<Long> result = repository.findIntroductionMemberIds(key);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Redis에 저장된 JSON을 올바르게 역직렬화하여 반환")
        void returnIntroductionMemberIdsWhenRedisHasValue() throws JsonProcessingException {
            // given
            String key = "testKey";
            String fullKey = "introduction:" + key;
            String json = "[1,2,3]";
            Set<Long> ids = Set.of(1L, 2L, 3L);

            when(redisTemplate.opsForValue()).thenReturn(valueOps);
            when(valueOps.get(fullKey)).thenReturn(json);
            when(mapper.readValue(eq(json), any(TypeReference.class))).thenReturn(ids);

            // when
            Set<Long> result = repository.findIntroductionMemberIds(key);

            // then
            assertThat(result).isEqualTo(ids);
        }

        @Test
        @DisplayName("Redis에 저장된 JSON 역직렬화 실패 시 예외 발생")
        void throwExceptionWhenDeserializationFailed() throws JsonProcessingException {
            // given
            String key = "testKey";
            String fullKey = "introduction:" + key;
            String json = "[1,2,3]";

            when(redisTemplate.opsForValue()).thenReturn(valueOps);
            when(valueOps.get(fullKey)).thenReturn(json);
            when(mapper.readValue(eq(json), any(TypeReference.class))).thenThrow(new JsonProcessingException("error") {});

            // when & then
            assertThatThrownBy(() -> repository.findIntroductionMemberIds(key))
                    .isInstanceOf(IntroductionMemberIdSerializationFailedException.class);
        }
    }
}
