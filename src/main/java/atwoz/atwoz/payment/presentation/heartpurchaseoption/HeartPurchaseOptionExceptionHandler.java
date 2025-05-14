package atwoz.atwoz.payment.presentation.heartpurchaseoption;

import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.payment.command.application.heartpurchaseoption.exception.HeartPurchaseOptionAlreadyExistsException;
import atwoz.atwoz.payment.command.application.order.exception.HeartPurchaseOptionNotFoundException;
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
            .body(BaseResponse.from(StatusType.INVALID_DUPLICATE_VALUE));
    }

    @ExceptionHandler(HeartPurchaseOptionNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleHeartPurchaseOptionNotFoundException(
        HeartPurchaseOptionNotFoundException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(404)
            .body(BaseResponse.from(StatusType.NOT_FOUND));
    }
}
