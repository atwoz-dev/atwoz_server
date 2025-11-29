package deepple.deepple.payment.presentation.heartpurchaseoption;

import deepple.deepple.common.enums.StatusType;
import deepple.deepple.common.response.BaseResponse;
import deepple.deepple.payment.command.application.heartpurchaseoption.exception.HeartPurchaseOptionAlreadyExistsException;
import deepple.deepple.payment.command.application.order.exception.HeartPurchaseOptionNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HeartPurchaseOptionExceptionHandler {

    @ExceptionHandler(HeartPurchaseOptionAlreadyExistsException.class)
    public ResponseEntity<BaseResponse<Void>> handleHeartPurchaseOptionAlreadyExistsException(
        HeartPurchaseOptionAlreadyExistsException e) {
        log.warn(e.getMessage());

        return ResponseEntity.badRequest()
            .body(BaseResponse.of(StatusType.INVALID_DUPLICATE_VALUE, e.getMessage()));
    }

    @ExceptionHandler(HeartPurchaseOptionNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleHeartPurchaseOptionNotFoundException(
        HeartPurchaseOptionNotFoundException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(404)
            .body(BaseResponse.of(StatusType.NOT_FOUND, e.getMessage()));
    }
}
