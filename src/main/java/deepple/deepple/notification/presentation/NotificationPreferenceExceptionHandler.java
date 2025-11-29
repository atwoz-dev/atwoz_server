package deepple.deepple.notification.presentation;

import deepple.deepple.common.enums.StatusType;
import deepple.deepple.common.response.BaseResponse;
import deepple.deepple.notification.command.application.InvalidNotificationTypeException;
import deepple.deepple.notification.command.application.NotificationPreferenceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class NotificationPreferenceExceptionHandler {

    @ExceptionHandler(NotificationPreferenceNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleNotificationPreferenceNotFoundException(
        NotificationPreferenceNotFoundException e
    ) {
        log.warn(e.getMessage());

        return ResponseEntity.status(404)
            .body(BaseResponse.of(StatusType.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(InvalidNotificationTypeException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidNotificationTypeException(
        InvalidNotificationTypeException e
    ) {
        log.warn(e.getMessage());

        return ResponseEntity.status(400)
            .body(BaseResponse.of(StatusType.BAD_REQUEST, e.getMessage()));
    }
}
