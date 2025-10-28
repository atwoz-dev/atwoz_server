package atwoz.atwoz.admin.presentation.suspension;

import atwoz.atwoz.admin.command.application.suspension.InvalidSuspensionStatusException;
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
public class SuspensionExceptionHandler {

    @ExceptionHandler(InvalidSuspensionStatusException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidSuspensionStatusException(
        InvalidSuspensionStatusException e
    ) {
        log.warn(e.getMessage());

        return ResponseEntity.badRequest()
            .body(BaseResponse.of(StatusType.INVALID_INPUT_VALUE, e.getMessage()));
    }
}
