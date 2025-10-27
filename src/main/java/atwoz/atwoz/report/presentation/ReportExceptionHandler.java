package atwoz.atwoz.report.presentation;

import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.report.command.application.exception.ReportAlreadyExistsException;
import atwoz.atwoz.report.command.application.exception.ReportNotFoundException;
import atwoz.atwoz.report.command.domain.InvalidReportException;
import atwoz.atwoz.report.command.domain.exception.InvalidReportReasonTypeException;
import atwoz.atwoz.report.command.domain.exception.InvalidReportResultException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ReportExceptionHandler {
    @ExceptionHandler(InvalidReportReasonTypeException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidReportReasonTypeException(
        InvalidReportReasonTypeException e) {
        log.warn("신고 사유가 잘못되었습니다. {}", e.getMessage());

        return ResponseEntity.badRequest().body(BaseResponse.of(StatusType.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(InvalidReportResultException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidReportResultException(
        InvalidReportResultException e) {
        log.warn("신고 결과가 잘못되었습니다. {}", e.getMessage());

        return ResponseEntity.badRequest().body(BaseResponse.of(StatusType.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(ReportAlreadyExistsException.class)
    public ResponseEntity<BaseResponse<Void>> handleReportAlreadyExistsException(
        ReportAlreadyExistsException e) {
        log.warn("이미 신고한 멤버입니다. {}", e.getMessage());

        return ResponseEntity.badRequest().body(BaseResponse.of(StatusType.INVALID_DUPLICATE_VALUE, e.getMessage()));
    }

    @ExceptionHandler(InvalidReportException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidReportException(InvalidReportException e) {
        log.warn("잘못된 신고입니다. {}", e.getMessage());

        return ResponseEntity.badRequest().body(BaseResponse.of(StatusType.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleReportNotFoundException(ReportNotFoundException e) {
        log.warn("해당 신고가 존재하지 않습니다. {}", e.getMessage());

        return ResponseEntity.status(404).body(BaseResponse.of(StatusType.NOT_FOUND, e.getMessage()));
    }
}
