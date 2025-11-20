package atwoz.atwoz.member.presentation.introduction;

import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.member.command.application.introduction.exception.*;
import atwoz.atwoz.member.command.domain.introduction.exception.InvalidAgeRangeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MemberIntroductionExceptionHandler {

    @ExceptionHandler(MemberIdealNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleMemberIdealNotFoundException(MemberIdealNotFoundException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(404)
            .body(BaseResponse.of(StatusType.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(InvalidAgeRangeException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidAgeRangeException(InvalidAgeRangeException e) {
        log.warn(e.getMessage());

        return ResponseEntity.badRequest()
            .body(BaseResponse.of(StatusType.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(IntroducedMemberNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleIntroducedMemberNotFoundException(
        IntroducedMemberNotFoundException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(404)
            .body(BaseResponse.of(StatusType.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(IntroducedMemberNotActiveException.class)
    public ResponseEntity<BaseResponse<Void>> handleIntroducedMemberNotActiveException(
        IntroducedMemberNotActiveException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(403)
            .body(BaseResponse.of(StatusType.FORBIDDEN, e.getMessage()));
    }

    @ExceptionHandler(IntroducedMemberBlockedException.class)
    public ResponseEntity<BaseResponse<Void>> handleIntroducedMemberBlockedException(
        IntroducedMemberBlockedException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(403)
            .body(BaseResponse.of(StatusType.FORBIDDEN, e.getMessage()));
    }

    @ExceptionHandler(MemberIntroductionAlreadyExistsException.class)
    public ResponseEntity<BaseResponse<Void>> handleMemberIntroductionAlreadyExistsException(
        MemberIntroductionAlreadyExistsException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(400)
            .body(BaseResponse.of(StatusType.BAD_REQUEST, e.getMessage()));
    }
}
