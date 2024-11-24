package awtoz.awtoz.global.auth.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class TokenExceptionHandler {

    public void handleException(HttpServletResponse response, Exception e) {
        setJsonResponse(response, HttpStatus.UNAUTHORIZED.value(), e.getMessage());
    }

    private void setJsonResponse(HttpServletResponse response, int statusCode, String message) {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("code", HttpStatus.UNAUTHORIZED.toString());
        body.put("message", message);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(body);
            response.getWriter().write(jsonBody);
        } catch (IOException ioException) {
            log.error(ioException.getMessage());
        }
    }
}
