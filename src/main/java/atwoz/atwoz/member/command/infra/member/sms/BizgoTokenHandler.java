package atwoz.atwoz.member.command.infra.member.sms;

import atwoz.atwoz.member.command.infra.member.sms.dto.BizgoAuthResponse;
import atwoz.atwoz.member.command.infra.member.sms.exception.BizgoAuthenticationException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;


@Service
public class BizgoTokenHandler {
    private ReentrantLock lock;

    private RestClient restClient;

    @Value("${bizgo.client-id}")
    private String clientId;
    @Value("${bizgo.client-password}")
    private String clientPassword;
    @Value("${bizgo.api-url}")
    private String apiUrl;

    private String authToken;
    private Date authTime;

    @PostConstruct
    public void init() {
        lock = new ReentrantLock();
        restClient = RestClient.create();
        setAuthToken();
    }

    public String getAuthToken() {
        if (isOver23Hours(authTime)) {
            reissueAuthToken();
        }
        return authToken;
    }

    private boolean isOver23Hours(Date authTime) {
        long now = System.currentTimeMillis();
        long diffMillis = now - authTime.getTime();
        long hours23 = 23L * 60 * 60 * 1000;
        return diffMillis >= hours23;
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

        authTime = new Date();
        authToken = response.data().token();
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
        if (response == null || response.code() == null || !response.code().equals(ResponseCode.SUCCESS.getCode())
            || response.data() == null
            || response.data().token() == null) {
            throw new BizgoAuthenticationException();
        }
    }
}
