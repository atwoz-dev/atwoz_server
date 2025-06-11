package atwoz.atwoz.member.command.infra.member.sms;

import atwoz.atwoz.member.command.infra.member.sms.dto.BizgoAuthResponse;
import atwoz.atwoz.member.command.infra.member.sms.dto.BizgoMessageRequest;
import atwoz.atwoz.member.command.infra.member.sms.dto.BizgoMessageResponse;
import atwoz.atwoz.member.command.infra.member.sms.exception.BizgoAuthenticationException;
import atwoz.atwoz.member.command.infra.member.sms.exception.BizgoMessageSendException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class BizgoMessanger {
    private final ReentrantLock lock = new ReentrantLock();
    @Value("${bizgo.client-id}")
    private String clientId;
    @Value("${bizgo.client-password}")
    private String clientPassword;
    @Value("${bizgo.from-phone-number}")
    private String fromPhoneNumber;
    @Value("${bizgo.api-url}")
    private String apiUrl;
    private String authToken;

    public void sendMessage(String message, String phoneNumber) {
        if (authToken == null) {
            resetAuthToken();
        }
        BizgoMessageResponse response = trySendMessageWithRetry(message, phoneNumber);

        if (isFailMessageResponse(response)) {
            throw new BizgoMessageSendException(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private BizgoMessageResponse trySendMessageWithRetry(String message, String phoneNumber) {
        int retryCount = 0;
        while (retryCount < 3) {
            try {
                BizgoMessageResponse response = sendRequest(message, phoneNumber);
                if (!isFailMessageResponse(response)) {
                    return response;
                }
            } catch (BizgoMessageSendException e) {
                if (e.getStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
                    resetAuthToken();
                }
            }
            retryCount++;
        }
        return null;
    }

    private BizgoMessageResponse sendRequest(String message, String phoneNumber) {
        String requestURL = apiUrl + "/send/sms";
        RestClient restClient = RestClient.create();
        return restClient.post()
            .uri(requestURL)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("Authorization", "Bearer " + authToken)
            .body(new BizgoMessageRequest(fromPhoneNumber, phoneNumber, message))
            .retrieve()
            .onStatus(HttpStatusCode::isError, (request, httpResponse) -> {
                    throw new BizgoMessageSendException(httpResponse.getStatusCode().value());
                }
            )
            .toEntity(BizgoMessageResponse.class)
            .getBody();
    }

    private void resetAuthToken() {
        lock.lock();
        try {
            setAuthToken();
        } finally {
            lock.unlock();
        }
    }

    private void setAuthToken() {
        String requestURL = apiUrl + "/auth/token";

        RestClient restClient = RestClient.create();
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
    }

    private void validateAuthResponse(BizgoAuthResponse response) {
        if (response == null || response.code() == null || !response.code().equals(ResponseCode.SUCCESS.getCode())
            || response.data() == null
            || response.data().token() == null) {
            throw new BizgoAuthenticationException();
        }
    }

    private boolean isFailMessageResponse(BizgoMessageResponse response) {
        return response == null || response.code() == null || !response.code().equals(ResponseCode.SUCCESS.getCode());
    }
}
