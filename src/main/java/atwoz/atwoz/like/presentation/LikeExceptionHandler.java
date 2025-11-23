package atwoz.atwoz.like.presentation;

import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.like.command.application.InvalidLikeLevelException;
import atwoz.atwoz.like.command.application.LikeAlreadyExistsException;
import atwoz.atwoz.like.command.application.LikeBlockedException;
import atwoz.atwoz.like.command.application.MemberNotFoundException;
import atwoz.atwoz.like.command.application.exception.LikeReceiverInactiveException;
import atwoz.atwoz.like.command.application.exception.LikeSameGenderException;
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

        return ResponseEntity.badRequest().body(BaseResponse.of(StatusType.INVALID_DUPLICATE_VALUE, e.getMessage()));
    }

    @ExceptionHandler(InvalidLikeLevelException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidLikeLevelException(InvalidLikeLevelException e) {
        log.warn(e.getMessage());

        return ResponseEntity.badRequest().body(BaseResponse.of(StatusType.INVALID_ENUM_VALUE, e.getMessage()));
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleMemberNotFoundException(MemberNotFoundException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(404).body(BaseResponse.of(StatusType.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(LikeReceiverInactiveException.class)
    public ResponseEntity<BaseResponse<Void>> handleLikeReceiverInactiveException(LikeReceiverInactiveException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(403).body(BaseResponse.of(StatusType.FORBIDDEN, e.getMessage()));
    }

    @ExceptionHandler(LikeBlockedException.class)
    public ResponseEntity<BaseResponse<Void>> handleLikeBlockedException(LikeBlockedException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(403).body(BaseResponse.of(StatusType.FORBIDDEN, e.getMessage()));
    }

    @ExceptionHandler(LikeSameGenderException.class)
    public ResponseEntity<BaseResponse<Void>> handleLikeSameGenderException(LikeSameGenderException e) {
        log.warn(e.getMessage());

        return ResponseEntity.badRequest().body(BaseResponse.of(StatusType.BAD_REQUEST, e.getMessage()));
    }
}
