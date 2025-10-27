package atwoz.atwoz.admin.presentation.screening;

import atwoz.atwoz.admin.command.application.screening.InvalidRejectionReasonException;
import atwoz.atwoz.admin.command.application.screening.ScreeningNotFoundException;
import atwoz.atwoz.admin.command.domain.screening.CannotRejectApprovedScreeningException;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
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
