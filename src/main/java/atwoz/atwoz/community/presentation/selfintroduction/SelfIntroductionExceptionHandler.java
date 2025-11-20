package atwoz.atwoz.community.presentation.selfintroduction;

import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.community.command.application.selfintroduction.exception.NotSelfIntroductionAuthorException;
import atwoz.atwoz.community.command.application.selfintroduction.exception.SelfIntroductionNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SelfIntroductionExceptionHandler {

    @ExceptionHandler(SelfIntroductionNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleSelfIntroductionNotFoundException(
        SelfIntroductionNotFoundException e) {
        log.warn("셀프 소개 조회에 실패하였습니다. {}", e.getMessage());

        return ResponseEntity.status(404)
            .body(BaseResponse.of(StatusType.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(NotSelfIntroductionAuthorException.class)
    public ResponseEntity<BaseResponse<Void>> handleNotMatchedMemberIdException(NotSelfIntroductionAuthorException e) {
        log.warn("셀프 소개 작성에 실패하였습니다. {}", e.getMessage());

        return ResponseEntity.status(401)
            .body(BaseResponse.of(StatusType.UNAUTHORIZED, e.getMessage()));
    }
}
