package atwoz.atwoz.common;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<List<String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return ResponseEntity.badRequest()
                .body(BaseResponse.of(StatusType.BAD_REQUEST, errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleGlobalException(Exception exception, HttpServletRequest request) {
        String requestInfo = "Request URI: " + request.getRequestURI() + ", Method: " + request.getMethod();
        log.error("Unexpected error occurred. RequestInfo: {}, ExceptionMessage: {}", requestInfo, exception.getMessage(), exception);

        return ResponseEntity.internalServerError()
                .body(BaseResponse.from(StatusType.INTERNAL_SERVER_ERROR));
    }
}