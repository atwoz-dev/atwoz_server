package atwoz.atwoz.member.command.infra.sms;

import atwoz.atwoz.member.command.infra.sms.dto.BizgoAuthResponse;
import atwoz.atwoz.member.command.infra.sms.dto.BizgoMessageRequest;
import atwoz.atwoz.member.command.infra.sms.dto.BizgoMessageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class BizgoMessanger {
    @Value("${bizgo.client-id}")
    private String CLIENT_ID;

    @Value("${bizgo.client-password}")
    private String CLIENT_PASSWORD;

    @Value("${bizgo.from-phone-number}")
    private String FROM_PHONE_NUMBER;

    @Value("${bizgo.api-url}")
    private String API_URL;

    private String AUTH_TOKEN;


    public boolean sendMessage(String message, String phoneNumber) {
        if (AUTH_TOKEN == null) {
            setAuthToken();
        }

        BizgoMessageResponse response = sendRequest(message, phoneNumber);
        String code = response.code();

        if (!ResponseCode.SUCCESS.getCode().equals(code)) {
            // TODO : 재시도 로직.
        }

        return true;
    }

    private BizgoMessageResponse sendRequest(String message, String phoneNumber) {
        String requestURL = API_URL + "/send/sms";
        RestClient restClient = RestClient.create();
        return restClient.post()
            .uri(requestURL)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("Authorization", "Bearer " + AUTH_TOKEN)
            .body(new BizgoMessageRequest(FROM_PHONE_NUMBER, phoneNumber, message))
            .retrieve()
            .toEntity(BizgoMessageResponse.class)
            .getBody();
    }

    private void setAuthToken() {
        String requestURL = API_URL + "/auth/token";

        RestClient restClient = RestClient.create();
        BizgoAuthResponse response = restClient.post()
            .uri(requestURL)
            .header("Accept", "application/json")
            .header("X-IB-Client-Id", CLIENT_ID)
            .header("X-IB-Client-Passwd", CLIENT_PASSWORD)
            .retrieve()
            .toEntity(BizgoAuthResponse.class)
            .getBody();

        if (response.code() != "A000" || response.data() == null || response.data().token() == null) {
            // Fail.
        }
        AUTH_TOKEN = response.data().token();
    }
}
