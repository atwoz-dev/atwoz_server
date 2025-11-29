package deepple.deepple.member.command.infra.member.sms;

import deepple.deepple.common.repository.RedissonLockRepository;
import deepple.deepple.member.command.infra.member.sms.dto.BizgoAuthResponse;
import deepple.deepple.member.command.infra.member.sms.exception.BizgoAuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class BizgoTokenHandler {
    private static final long EXPIRE_TIME_SECOND = 23 * 60 * 60;
    private static final String KEY = "BIZGO_AUTH_TOKEN";
    private static final int WAIT_TIME = 3;
    private static final int LEASE_TIME = 5;
    private final RedisTemplate<String, String> redisTemplate;
    private final RedissonLockRepository redissonLockRepository;
    private final RestClient restClient;
    @Value("${bizgo.client-id}")
    private String clientId;
    @Value("${bizgo.client-password}")
    private String clientPassword;
    @Value("${bizgo.api-url}")
    private String apiUrl;

    public String getAuthToken() {
        String token = redisTemplate.opsForValue().get(KEY);
        if (token == null) {
            redissonLockRepository.withLock(() -> {
                if (redisTemplate.opsForValue().get(KEY) != null) {
                    return;
                }
                setAuthToken();
            }, KEY, WAIT_TIME, LEASE_TIME);

            return redisTemplate.opsForValue().get(KEY);
        } else {
            return token;
        }
    }

    private void setAuthToken() {
        String requestURL = apiUrl + "/auth/token";

        BizgoAuthResponse response = restClient.post()
            .uri(requestURL)
            .header("Accept", "application/json")
            .header("X-IB-Client-Id", clientId)
            .header("X-IB-Client-Passwd", clientPassword)
            .retrieve()
            .onStatus(HttpStatusCode::isError, (request, httpResponse) -> {
                    throw new BizgoAuthenticationException();
                }
            )
            .toEntity(BizgoAuthResponse.class)
            .getBody();

        validateAuthResponse(response);

        String authToken = response.data().token();
        redisTemplate.opsForValue().set(KEY, authToken);
        redisTemplate.expire(KEY, EXPIRE_TIME_SECOND, TimeUnit.SECONDS);
    }

    private void validateAuthResponse(BizgoAuthResponse response) {
        if (response == null || response.code() == null || response.data() == null || response.data().token() == null) {
            throw new BizgoAuthenticationException();
        }
    }
}
