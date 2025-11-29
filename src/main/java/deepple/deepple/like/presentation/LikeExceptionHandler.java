package deepple.deepple.like.presentation;

import deepple.deepple.common.enums.StatusType;
import deepple.deepple.common.response.BaseResponse;
import deepple.deepple.like.command.application.InvalidLikeLevelException;
import deepple.deepple.like.command.application.LikeAlreadyExistsException;
import deepple.deepple.like.command.application.LikeBlockedException;
import deepple.deepple.like.command.application.MemberNotFoundException;
import deepple.deepple.like.command.application.exception.LikeReceiverInactiveException;
import deepple.deepple.like.command.application.exception.LikeSameGenderException;
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
