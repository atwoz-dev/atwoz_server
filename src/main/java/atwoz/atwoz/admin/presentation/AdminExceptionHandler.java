package atwoz.atwoz.admin.presentation;

import atwoz.atwoz.admin.application.exception.DuplicateEmailException;
import atwoz.atwoz.common.presentation.BaseResponse;
import atwoz.atwoz.common.presentation.StatusType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class AdminExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<BaseResponse<Void>> handleDuplicateEmailException(DuplicateEmailException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.from(StatusType.INVALID_DUPLICATE_VALUE));
    }
}
