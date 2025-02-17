package atwoz.atwoz.interview.presentation.answer;

import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.interview.command.application.answer.exception.InterviewAnserAlreadyExistsException;
import atwoz.atwoz.interview.command.application.answer.exception.InterviewQuestionIsNotPublicException;
import atwoz.atwoz.interview.command.application.answer.exception.InterviewQuestionNotFoundException;
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
    public ResponseEntity<BaseResponse<Void>> handleInterviewQuestionNotFoundException(InterviewQuestionNotFoundException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(404)
                .body(BaseResponse.from(StatusType.NOT_FOUND));
    }

    @ExceptionHandler(InterviewQuestionIsNotPublicException.class)
    public ResponseEntity<BaseResponse<Void>> handleInterviewQuestionIsNotPublicException(InterviewQuestionIsNotPublicException e) {
        log.warn(e.getMessage());

        return ResponseEntity.badRequest()
                .body(BaseResponse.from(StatusType.BAD_REQUEST));
    }

    @ExceptionHandler(InterviewAnserAlreadyExistsException.class)
    public ResponseEntity<BaseResponse<Void>> handleInterviewAnserAlreadyExistsException(InterviewAnserAlreadyExistsException e) {
        log.warn(e.getMessage());

        return ResponseEntity.badRequest()
                .body(BaseResponse.from(StatusType.BAD_REQUEST));
    }
}
