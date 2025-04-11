package atwoz.atwoz.interview.presentation.question;

import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.interview.command.application.question.exception.InterviewQuestionNotFoundException;
import atwoz.atwoz.interview.command.application.question.exception.InterviewQuestionAlreadyExistsException;
import atwoz.atwoz.interview.command.domain.question.exception.InvalidInterviewCategoryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class InterviewQuestionExceptionHandler {

    @ExceptionHandler(InterviewQuestionAlreadyExistsException.class)
    public ResponseEntity<BaseResponse<Void>> handleInterviewQuestionAlreadyExistsException(InterviewQuestionAlreadyExistsException e) {
        log.warn(e.getMessage());

        return ResponseEntity.badRequest()
                .body(BaseResponse.from(StatusType.INVALID_DUPLICATE_VALUE));
    }

    @ExceptionHandler(InvalidInterviewCategoryException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidInterviewCategoryException(InvalidInterviewCategoryException e) {
        log.warn(e.getMessage());

        return ResponseEntity.badRequest()
                .body(BaseResponse.from(StatusType.INVALID_TYPE_VALUE));
    }

    @ExceptionHandler(InterviewQuestionNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleInterviewQuestionNotFoundException(InterviewQuestionNotFoundException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(404)
                .body(BaseResponse.from(StatusType.NOT_FOUND));
    }
}
