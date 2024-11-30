package atwoz.atwoz.common.exception;

import atwoz.atwoz.common.presentation.BaseResponse;
import atwoz.atwoz.common.presentation.StatusType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleGlobalException(Exception exception, HttpServletRequest request) {
        String requestInfo = "Request URI: " + request.getRequestURI() + ", Method: " + request.getMethod();
        log.error("Unexpected error occurred. RequestInfo: {}, ExceptionMessage: {}", requestInfo, exception.getMessage(), exception);
        return ResponseEntity.internalServerError().body(new BaseResponse<>(StatusType.INTERNAL_SERVER_ERROR));
    }
}