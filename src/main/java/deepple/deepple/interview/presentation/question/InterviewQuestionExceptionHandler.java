package deepple.deepple.interview.presentation.question;

import deepple.deepple.common.enums.StatusType;
import deepple.deepple.common.response.BaseResponse;
import deepple.deepple.interview.command.application.question.exception.InterviewQuestionAlreadyExistsException;
import deepple.deepple.interview.command.application.question.exception.InterviewQuestionNotFoundException;
import deepple.deepple.interview.command.domain.question.exception.InvalidInterviewCategoryException;
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
    public ResponseEntity<BaseResponse<Void>> handleInterviewQuestionAlreadyExistsException(
        InterviewQuestionAlreadyExistsException e) {
        log.warn(e.getMessage());

        return ResponseEntity.badRequest()
            .body(BaseResponse.of(StatusType.INVALID_DUPLICATE_VALUE, e.getMessage()));
    }

    @ExceptionHandler(InvalidInterviewCategoryException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidInterviewCategoryException(
        InvalidInterviewCategoryException e) {
        log.warn(e.getMessage());

        return ResponseEntity.badRequest()
            .body(BaseResponse.of(StatusType.INVALID_TYPE_VALUE, e.getMessage()));
    }

    @ExceptionHandler(InterviewQuestionNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleInterviewQuestionNotFoundException(
        InterviewQuestionNotFoundException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(404)
            .body(BaseResponse.of(StatusType.NOT_FOUND, e.getMessage()));
    }
}
