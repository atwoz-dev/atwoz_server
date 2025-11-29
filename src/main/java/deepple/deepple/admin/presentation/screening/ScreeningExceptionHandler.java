package deepple.deepple.admin.presentation.screening;

import deepple.deepple.admin.command.application.screening.InvalidRejectionReasonException;
import deepple.deepple.admin.command.application.screening.ScreeningNotFoundException;
import deepple.deepple.admin.command.domain.screening.CannotRejectApprovedScreeningException;
import deepple.deepple.common.enums.StatusType;
import deepple.deepple.common.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ScreeningExceptionHandler {

    @ExceptionHandler(CannotRejectApprovedScreeningException.class)
    public ResponseEntity<BaseResponse<Void>> handleCannotRejectApprovedScreeningException(
        CannotRejectApprovedScreeningException e) {
        log.warn(e.getMessage());

        return ResponseEntity.badRequest()
            .body(BaseResponse.of(StatusType.CANNOT_BE_EDITED, e.getMessage()));
    }

    @ExceptionHandler(ScreeningNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleScreeningNotFoundException(ScreeningNotFoundException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(404)
            .body(BaseResponse.of(StatusType.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(InvalidRejectionReasonException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidRejectionReasonException(InvalidRejectionReasonException e) {
        log.warn(e.getMessage());

        return ResponseEntity.badRequest()
            .body(BaseResponse.of(StatusType.INVALID_INPUT_VALUE, e.getMessage()));
    }
}
