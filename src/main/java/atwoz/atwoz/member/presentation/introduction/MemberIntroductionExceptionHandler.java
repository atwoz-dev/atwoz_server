package atwoz.atwoz.member.presentation.introduction;

import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.member.command.application.introduction.exception.IntroducedMemberNotActiveException;
import atwoz.atwoz.member.command.application.introduction.exception.IntroducedMemberNotFoundException;
import atwoz.atwoz.member.command.application.introduction.exception.MemberIdealNotFoundException;
import atwoz.atwoz.member.command.application.introduction.exception.MemberIntroductionAlreadyExistsException;
import atwoz.atwoz.member.command.domain.introduction.exception.InvalidAgeRangeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class MemberIntroductionExceptionHandler {

    @ExceptionHandler(MemberIdealNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleMemberIdealNotFoundException(MemberIdealNotFoundException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(404)
            .body(BaseResponse.from(StatusType.NOT_FOUND));
    }

    @ExceptionHandler(InvalidAgeRangeException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidAgeRangeException(InvalidAgeRangeException e) {
        log.warn(e.getMessage());

        return ResponseEntity.badRequest()
            .body(BaseResponse.from(StatusType.BAD_REQUEST));
    }

    @ExceptionHandler(IntroducedMemberNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleIntroducedMemberNotFoundException(
        IntroducedMemberNotFoundException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(404)
            .body(BaseResponse.from(StatusType.NOT_FOUND));
    }

    @ExceptionHandler(IntroducedMemberNotActiveException.class)
    public ResponseEntity<BaseResponse<Void>> handleIntroducedMemberNotActiveException(
        IntroducedMemberNotActiveException e) {
        log.warn(e.getMessage());

        return ResponseEntity.badRequest()
            .body(BaseResponse.from(StatusType.BAD_REQUEST));
    }

    @ExceptionHandler(MemberIntroductionAlreadyExistsException.class)
    public ResponseEntity<BaseResponse<Void>> handleMemberIntroductionAlreadyExistsException(
        MemberIntroductionAlreadyExistsException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(400)
            .body(BaseResponse.of(StatusType.BAD_REQUEST, e.getMessage()));
    }
}
