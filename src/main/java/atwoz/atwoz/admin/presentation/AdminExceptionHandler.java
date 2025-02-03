package atwoz.atwoz.admin.presentation;

import atwoz.atwoz.admin.command.application.admin.exception.AdminNotFoundException;
import atwoz.atwoz.admin.command.application.admin.exception.DuplicateEmailException;
import atwoz.atwoz.admin.command.domain.admin.exception.IncorrectPasswordException;
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

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<BaseResponse<Void>> handlePasswordMismatchException(IncorrectPasswordException e) {
        log.warn("관리자 로그인에 실패했습니다. {}", e.getMessage());

        return ResponseEntity.status(401)
                .body(BaseResponse.from(StatusType.UNAUTHORIZED));
    }
}