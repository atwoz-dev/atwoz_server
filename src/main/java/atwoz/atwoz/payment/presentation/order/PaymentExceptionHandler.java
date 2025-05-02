package atwoz.atwoz.payment.presentation.order;

import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.payment.command.application.order.exception.InvalidOrderException;
import atwoz.atwoz.payment.command.application.order.exception.OrderAlreadyExistsException;
import atwoz.atwoz.payment.command.infra.order.exception.AppStoreClientException;
import atwoz.atwoz.payment.command.infra.order.exception.InvalidTransactionIdException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PaymentExceptionHandler {
    @ExceptionHandler(InvalidOrderException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidOrderException(InvalidOrderException e) {
        log.warn("잘못된 주문입니다. {}", e.getMessage());

        return ResponseEntity.status(400)
            .body(BaseResponse.from(StatusType.BAD_REQUEST));
    }

    @ExceptionHandler(OrderAlreadyExistsException.class)
    public ResponseEntity<BaseResponse<Void>> handleOrderAlreadyExistsException(OrderAlreadyExistsException e) {
        log.warn("이미 처리된 주문입니다. {}", e.getMessage());

        return ResponseEntity.status(400)
            .body(BaseResponse.from(StatusType.BAD_REQUEST));
    }

    @ExceptionHandler(InvalidTransactionIdException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidTransactionIdException(InvalidTransactionIdException e) {
        log.warn("잘못된 Transaction ID입니다. {}", e.getMessage());

        return ResponseEntity.status(400)
            .body(BaseResponse.from(StatusType.BAD_REQUEST));
    }

    @ExceptionHandler(AppStoreClientException.class)
    public ResponseEntity<BaseResponse<Void>> handleAppStoreClientException(AppStoreClientException e) {
        log.warn("앱스토어 서버와 통신 중 오류가 발생했습니다. {}", e.getMessage());

        return ResponseEntity.status(500)
            .body(BaseResponse.from(StatusType.INTERNAL_SERVER_ERROR));
    }
}
