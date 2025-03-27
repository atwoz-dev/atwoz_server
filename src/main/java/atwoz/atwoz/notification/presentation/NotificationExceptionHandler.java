package atwoz.atwoz.notification.presentation;

import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.notification.command.application.notification.ReceiverNotificationSettingNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class NotificationExceptionHandler {

    @ExceptionHandler(ReceiverNotificationSettingNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleReceiverNotificationSettingNotFoundException(ReceiverNotificationSettingNotFoundException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(404)
                .body(BaseResponse.from(StatusType.NOT_FOUND));
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleMemberNotFoundException(MemberNotFoundException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(404)
                .body(BaseResponse.from(StatusType.NOT_FOUND));
    }
}
