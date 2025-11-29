package deepple.deepple.heart.presentation.heartusagepolicy;

import deepple.deepple.common.enums.StatusType;
import deepple.deepple.common.response.BaseResponse;
import deepple.deepple.heart.command.domain.hearttransaction.exception.InsufficientHeartBalanceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HeartUsagePolicyExceptionHandler {

    @ExceptionHandler(InsufficientHeartBalanceException.class)
    public ResponseEntity<BaseResponse<Void>> handleInsufficientHeartBalanceException(
        InsufficientHeartBalanceException e) {
        log.warn("하트 잔액이 부족합니다. {}", e.getMessage());

        return ResponseEntity.badRequest()
            .body(BaseResponse.of(StatusType.INSUFFICIENT_HEARTS, e.getMessage()));
    }
}
