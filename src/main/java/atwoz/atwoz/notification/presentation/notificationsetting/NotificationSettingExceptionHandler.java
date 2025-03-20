package atwoz.atwoz.notification.presentation.notificationsetting;

import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.notification.command.application.notifiactionsetting.NotificationSettingNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class NotificationSettingExceptionHandler {

    @ExceptionHandler(NotificationSettingNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleNotificationSettingNotFoundException(NotificationSettingNotFoundException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(404)
                .body(BaseResponse.from(StatusType.NOT_FOUND));
    }
}
