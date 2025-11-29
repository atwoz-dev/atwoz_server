package deepple.deepple.community.presentation.profileexchange;

import deepple.deepple.common.enums.StatusType;
import deepple.deepple.common.response.BaseResponse;
import deepple.deepple.community.command.application.profileexchange.exception.*;
import deepple.deepple.community.command.domain.profileexchange.exception.InvalidProfileExchangeStatusException;
import deepple.deepple.community.command.domain.profileexchange.exception.SelfProfileExchangeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ProfileExchangeExceptionHandler {

    @ExceptionHandler(ProfileExchangeAlreadyExistsException.class)
    public ResponseEntity<BaseResponse<Void>> handleProfileExchangeAlreadyExistsException(
        ProfileExchangeAlreadyExistsException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(400)
            .body(BaseResponse.of(StatusType.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(ProfileExchangeNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleProfileExchangeNotFoundException(
        ProfileExchangeNotFoundException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(404)
            .body(BaseResponse.of(StatusType.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(ProfileExchangeResponderMismatchException.class)
    public ResponseEntity<BaseResponse<Void>> handleProfileExchangeResponderMismatchException(
        ProfileExchangeResponderMismatchException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(400)
            .body(BaseResponse.of(StatusType.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(SelfProfileExchangeException.class)
    public ResponseEntity<BaseResponse<Void>> handleSelfProfileExchangeException(
        SelfProfileExchangeException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(400)
            .body(BaseResponse.of(StatusType.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(InvalidProfileExchangeStatusException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidProfileExchangeStatusException(
        InvalidProfileExchangeStatusException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(400)
            .body(BaseResponse.of(StatusType.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(ProfileExchangeResponderNotActiveException.class)
    public ResponseEntity<BaseResponse<Void>> handleProfileExchangeResponderNotActiveException(
        ProfileExchangeResponderNotActiveException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(403)
            .body(BaseResponse.of(StatusType.FORBIDDEN, e.getMessage()));
    }

    @ExceptionHandler(ProfileExchangeHasBlockedException.class)
    public ResponseEntity<BaseResponse<Void>> handleProfileExchangeHasBlockedException(
        ProfileExchangeHasBlockedException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(403)
            .body(BaseResponse.of(StatusType.FORBIDDEN, e.getMessage()));
    }
}
