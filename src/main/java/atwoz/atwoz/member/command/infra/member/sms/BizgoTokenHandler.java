package atwoz.atwoz.member.command.infra.member.sms;

import atwoz.atwoz.member.command.infra.member.sms.dto.BizgoAuthResponse;
import atwoz.atwoz.member.command.infra.member.sms.exception.BizgoAuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;


@Service
@RequiredArgsConstructor
public class BizgoTokenHandler {
    private static final long HOURS23 = 23 * 60 * 60 * 1000L;
    private final ReentrantLock lock = new ReentrantLock();
    private final RestClient restClient;
    @Value("${bizgo.client-id}")
    private String clientId;
    @Value("${bizgo.client-password}")
    private String clientPassword;
    @Value("${bizgo.api-url}")
    private String apiUrl;

    private volatile String authToken;
    private volatile Date authTime;

    public String getAuthToken() {
        if (isOver23Hours(authTime)) {
            reissueAuthToken();
        }
        return authToken;
    }

    private boolean isOver23Hours(Date authTime) {
        if (authTime == null) {
            return true;
        }
        long now = System.currentTimeMillis();
        long diffMillis = now - authTime.getTime();
        return diffMillis >= HOURS23;
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

        authToken = response.data().token();
        authTime = new Date();
    }

    private void reissueAuthToken() {
        lock.lock();
        try {
            if (isOver23Hours(authTime)) {
                setAuthToken();
            }
        } finally {
            lock.unlock();
        }
    }

    private void validateAuthResponse(BizgoAuthResponse response) {
        if (response == null || response.code() == null || response.data() == null || response.data().token() == null) {
            throw new BizgoAuthenticationException();
        }
    }
}
