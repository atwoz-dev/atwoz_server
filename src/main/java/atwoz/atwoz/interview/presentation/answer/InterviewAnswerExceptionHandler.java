package atwoz.atwoz.interview.presentation.answer;

import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.interview.command.application.answer.exception.InterviewAnswerAccessDeniedException;
import atwoz.atwoz.interview.command.application.answer.exception.InterviewAnswerAlreadyExistsException;
import atwoz.atwoz.interview.command.application.answer.exception.InterviewAnswerNotFoundException;
import atwoz.atwoz.interview.command.application.question.exception.InterviewQuestionIsNotPublicException;
import atwoz.atwoz.interview.command.application.question.exception.InterviewQuestionNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class InterviewAnswerExceptionHandler {

    @ExceptionHandler(InterviewQuestionNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleInterviewQuestionNotFoundException(
        InterviewQuestionNotFoundException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(404)
            .body(BaseResponse.of(StatusType.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(InterviewQuestionIsNotPublicException.class)
    public ResponseEntity<BaseResponse<Void>> handleInterviewQuestionIsNotPublicException(
        InterviewQuestionIsNotPublicException e) {
        log.warn(e.getMessage());

        return ResponseEntity.badRequest()
            .body(BaseResponse.of(StatusType.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(InterviewAnswerAlreadyExistsException.class)
    public ResponseEntity<BaseResponse<Void>> handleInterviewAnserAlreadyExistsException(
        InterviewAnswerAlreadyExistsException e) {
        log.warn(e.getMessage());

        return ResponseEntity.badRequest()
            .body(BaseResponse.of(StatusType.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(InterviewAnswerNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleInterviewAnswerNotFoundException(
        InterviewAnswerNotFoundException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(404)
            .body(BaseResponse.of(StatusType.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(InterviewAnswerAccessDeniedException.class)
    public ResponseEntity<BaseResponse<Void>> handleInterviewAnswerAccessDeniedException(
        InterviewAnswerAccessDeniedException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(403)
            .body(BaseResponse.of(StatusType.FORBIDDEN, e.getMessage()));
    }
}
