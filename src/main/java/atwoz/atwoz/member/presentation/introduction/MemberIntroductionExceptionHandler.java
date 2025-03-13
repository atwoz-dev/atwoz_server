package atwoz.atwoz.member.presentation.introduction;

import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.member.command.application.introduction.exception.MemberIdealNotFoundException;
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
}
