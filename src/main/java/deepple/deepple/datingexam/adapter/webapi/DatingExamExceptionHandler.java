package deepple.deepple.datingexam.adapter.webapi;

import deepple.deepple.common.enums.StatusType;
import deepple.deepple.common.response.BaseResponse;
import deepple.deepple.datingexam.adapter.encoding.DatingExamEncodingFailedException;
import deepple.deepple.datingexam.application.exception.InvalidDatingExamSubmitRequestException;
import deepple.deepple.datingexam.domain.exception.InvalidDatingExamAnswerContentException;
import deepple.deepple.datingexam.domain.exception.InvalidDatingExamQuestionContentException;
import deepple.deepple.datingexam.domain.exception.InvalidDatingExamSubmitAnswersException;
import deepple.deepple.datingexam.domain.exception.InvalidSubjectNameException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DatingExamExceptionHandler {

    @ExceptionHandler(InvalidSubjectNameException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidSubjectNameException(InvalidSubjectNameException e) {
        log.warn("연애 모의고사 과목 이름이 잘못되었습니다. {}", e.getMessage());

        return ResponseEntity.badRequest()
            .body(BaseResponse.of(StatusType.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(InvalidDatingExamSubmitAnswersException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidDatingExamSubmitAnswersException(
        InvalidDatingExamSubmitAnswersException e) {
        log.warn("연애 모의고사 답안 제출이 잘못되었습니다. {}", e.getMessage());

        return ResponseEntity.badRequest()
            .body(BaseResponse.of(StatusType.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(InvalidDatingExamQuestionContentException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidDatingExamQuestionContentException(
        InvalidDatingExamQuestionContentException e) {
        log.warn("연애 모의고사 질문 내용이 잘못되었습니다. {}", e.getMessage());

        return ResponseEntity.badRequest()
            .body(BaseResponse.of(StatusType.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(InvalidDatingExamAnswerContentException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidDatingExamAnswerContentException(
        InvalidDatingExamAnswerContentException e) {
        log.warn("연애 모의고사 답안 내용이 잘못되었습니다. {}", e.getMessage());

        return ResponseEntity.badRequest()
            .body(BaseResponse.of(StatusType.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(InvalidDatingExamSubmitRequestException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidDatingExamSubmitRequestException(
        InvalidDatingExamSubmitRequestException e) {
        log.warn("연애 모의고사 제출 요청이 잘못되었습니다. {}", e.getMessage());

        return ResponseEntity.badRequest()
            .body(BaseResponse.of(StatusType.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(DatingExamEncodingFailedException.class)
    public ResponseEntity<BaseResponse<Void>> handleDatingExamEncodingFailedException(
        DatingExamEncodingFailedException e) {
        log.error("연애 모의고사 답안 인코딩에 실패했습니다. {}", e.getMessage(), e);

        return ResponseEntity.internalServerError()
            .body(BaseResponse.from(StatusType.INTERNAL_SERVER_ERROR));
    }
}
