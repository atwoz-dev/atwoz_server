package deepple.deepple.admin.presentation.warning;

import deepple.deepple.admin.command.application.warning.InvalidWarningReasonTypeException;
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
public class WarningExceptionHandler {

    @ExceptionHandler(InvalidWarningReasonTypeException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidWarningReasonTypeException(
        InvalidWarningReasonTypeException e
    ) {
        log.warn(e.getMessage());

        return ResponseEntity.badRequest()
            .body(BaseResponse.of(StatusType.INVALID_INPUT_VALUE, e.getMessage()));
    }
}
