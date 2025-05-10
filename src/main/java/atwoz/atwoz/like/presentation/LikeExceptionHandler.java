package atwoz.atwoz.like.presentation;

import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.like.command.application.InvalidLikeLevelException;
import atwoz.atwoz.like.command.application.LikeAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LikeExceptionHandler {

    @ExceptionHandler(LikeAlreadyExistsException.class)
    public ResponseEntity<BaseResponse<Void>> handleLikeAlreadyExistsException(LikeAlreadyExistsException e) {
        log.warn(e.getMessage());

        return ResponseEntity.badRequest().body(BaseResponse.from(StatusType.INVALID_DUPLICATE_VALUE));
    }

    @ExceptionHandler(InvalidLikeLevelException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidLikeLevelException(InvalidLikeLevelException e) {
        log.warn(e.getMessage());

        return ResponseEntity.badRequest().body(BaseResponse.from(StatusType.INVALID_ENUM_VALUE));
    }
}
