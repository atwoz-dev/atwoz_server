package atwoz.atwoz.member.command.infra.member.sms;

import atwoz.atwoz.member.command.infra.member.sms.dto.BizgoAuthResponse;
import atwoz.atwoz.member.command.infra.member.sms.dto.BizgoMessageRequest;
import atwoz.atwoz.member.command.infra.member.sms.dto.BizgoMessageResponse;
import atwoz.atwoz.member.command.infra.member.sms.exception.BizgoMessageSendException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
            lock.lock();
            try {
                if (authToken == null) {
                    setAuthToken();
                }
            } finally {
                lock.unlock();
            }
        }
        BizgoMessageResponse response = sendRequest(message, phoneNumber);

        // 재시도 로직.
        retryWhenResponseError(response, message, phoneNumber);
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
            .onStatus(HttpStatusCode::isError, (request, httpResponse) ->
                httpResponse.getBody()
            )
            .toEntity(BizgoMessageResponse.class)
            .getBody();
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
            .onStatus(HttpStatusCode::isError, (request, httpResponse) ->
                httpResponse.getBody()
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
            throw new BizgoMessageSendException();
        }
    }

    private void retryWhenResponseError(BizgoMessageResponse response, String message, String phoneNumber) {
        int retryCount = 0;
        while (isFailMessageResponse(response) && retryCount < 3) {
            response = sendRequest(message, phoneNumber);
            retryCount++;
        }

        if (isFailMessageResponse(response)) {
            throw new BizgoMessageSendException();
        }
    }

    private boolean isFailMessageResponse(BizgoMessageResponse response) {
        return response == null || response.code() == null || !response.code().equals(ResponseCode.SUCCESS.getCode());
    }
}
