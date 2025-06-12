package atwoz.atwoz.member.command.infra.member.sms;

import atwoz.atwoz.member.command.infra.member.sms.dto.BizgoMessageRequest;
import atwoz.atwoz.member.command.infra.member.sms.exception.BizgoMessageSendException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


@Service
@RequiredArgsConstructor
public class BizgoMessanger {
    private final RestClient restClient = RestClient.create();

    private final BizgoTokenHandler bizgoTokenHandler;

    @Value("${bizgo.from-phone-number}")
    private String fromPhoneNumber;
    @Value("${bizgo.api-url}")
    private String apiUrl;

    public void sendMessage(String message, String phoneNumber) {
        trySendMessageWithRetry(message, phoneNumber);
    }

    private void trySendMessageWithRetry(String message, String phoneNumber) {
        String authToken = bizgoTokenHandler.getAuthToken();

        try {
            sendRequest(message, phoneNumber, authToken);
        } catch (BizgoMessageSendException e) {
            if (e.getStatusCode() == ResponseCode.EXPIRED_TOKEN.getCode()) {
                authToken = bizgoTokenHandler.getAuthToken();
                sendRequest(message, phoneNumber, authToken);
            } else {
                throw new BizgoMessageSendException(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }
    }

    private void sendRequest(String message, String phoneNumber, String authToken) {
        String requestURL = apiUrl + "/send/sms";

        restClient.post()
            .uri(requestURL)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("Authorization", "Bearer " + authToken)
            .body(new BizgoMessageRequest(fromPhoneNumber, phoneNumber, message))
            .retrieve()
            .onStatus(HttpStatusCode::isError, (request, httpResponse) -> {
                    throw new BizgoMessageSendException(httpResponse.getStatusCode().value());
                }
            );
    }
}
