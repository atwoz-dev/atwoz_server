package atwoz.atwoz.auth.presentation.filter;

import atwoz.atwoz.common.enums.StatusType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResponseHandler {

    private static final String APPLICATION_JSON = "application/json";
    private static final String UTF_8 = "UTF-8";

    private final ObjectMapper objectMapper;

    public void setResponse(HttpServletResponse response, StatusType statusType) {
        setResponseHeader(response, statusType.getStatus(), APPLICATION_JSON, UTF_8);
        setResponseBody(response, statusType.getStatus(), statusType.getCode(), statusType.getMessage());
    }

    private void setResponseHeader(HttpServletResponse response, int status, String contentType, String characterEncoding) {
        response.setStatus(status);
        response.setContentType(contentType);
        response.setCharacterEncoding(characterEncoding);
    }

    private void setResponseBody(HttpServletResponse response, int status, String code, String message) {
        try (PrintWriter writer = response.getWriter()) {
            String jsonBody = makeJsonBody(status, code, message);
            writer.write(jsonBody);
            writer.flush();
        } catch (IOException e) {
            log.error("응답 바디 설정에 실패했습니다: {}", e.getMessage());
        }
    }

    private String makeJsonBody(int status, String code, String message) throws JsonProcessingException {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status);
        body.put("code", code);
        body.put("message", message);
        return objectMapper.writeValueAsString(body);
    }
}
