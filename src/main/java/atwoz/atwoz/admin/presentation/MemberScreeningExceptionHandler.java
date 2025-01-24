package atwoz.atwoz.admin.presentation;

import atwoz.atwoz.admin.command.domain.memberscreening.CannotRejectApprovedScreeningException;
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
public class MemberScreeningExceptionHandler {

    @ExceptionHandler(CannotRejectApprovedScreeningException.class)
    public ResponseEntity<BaseResponse<Void>> handleCannotRejectApprovedScreeningException(CannotRejectApprovedScreeningException e) {
        log.warn(e.getMessage());

        return ResponseEntity.badRequest()
                .body(BaseResponse.from(StatusType.CANNOT_BE_EDITED));
    }
}
