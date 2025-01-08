package atwoz.atwoz.admin.presentation;

import atwoz.atwoz.admin.application.exception.AdminNotFoundException;
import atwoz.atwoz.admin.application.exception.DuplicateEmailException;
import atwoz.atwoz.admin.application.exception.PasswordMismatchException;
import atwoz.atwoz.common.BaseResponse;
import atwoz.atwoz.common.StatusType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AdminExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<BaseResponse<Void>> handleDuplicateEmailException(DuplicateEmailException e) {
        log.warn("관리자 회원가입에 실패했습니다. {}", e.getMessage());

        return ResponseEntity.badRequest()
                .body(BaseResponse.from(StatusType.INVALID_DUPLICATE_VALUE));
    }

    @ExceptionHandler(AdminNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleAdminNotFoundException(AdminNotFoundException e) {
        log.warn("관리자 로그인에 실패했습니다. {}", e.getMessage());

        return ResponseEntity.status(401)
                .body(BaseResponse.from(StatusType.UNAUTHORIZED));
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<BaseResponse<Void>> handlePasswordMismatchException(PasswordMismatchException e) {
        log.warn("관리자 로그인에 실패했습니다. {}", e.getMessage());

        return ResponseEntity.status(401)
                .body(BaseResponse.from(StatusType.UNAUTHORIZED));
    }
}