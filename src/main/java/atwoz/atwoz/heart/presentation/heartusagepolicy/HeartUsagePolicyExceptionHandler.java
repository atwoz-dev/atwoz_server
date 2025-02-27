package atwoz.atwoz.heart.presentation.heartusagepolicy;

import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.heart.command.domain.hearttransaction.exception.InsufficientHeartBalanceException;
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
    public ResponseEntity<BaseResponse<Void>> handleInsufficientHeartBalanceException(InsufficientHeartBalanceException e) {
        log.warn("하트 잔액이 부족합니다. {}", e.getMessage());

        return ResponseEntity.badRequest()
                .body(BaseResponse.from(StatusType.INSUFFICIENT_HEARTS));
    }
}
