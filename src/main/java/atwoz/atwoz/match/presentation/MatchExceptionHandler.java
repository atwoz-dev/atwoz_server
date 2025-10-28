package atwoz.atwoz.match.presentation;

import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.match.command.application.match.exception.ExistsMatchException;
import atwoz.atwoz.match.command.application.match.exception.InvalidMatchUpdateException;
import atwoz.atwoz.match.command.application.match.exception.MatchNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class MatchExceptionHandler {

    @ExceptionHandler(ExistsMatchException.class)
    public ResponseEntity<BaseResponse<Void>> handleExistsMatchException(ExistsMatchException e) {
        log.warn("매치 요청에 실패하였습니다. {}", e.getMessage());

        return ResponseEntity.badRequest()
            .body(BaseResponse.of(StatusType.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(MatchNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleMatchNotFoundException(MatchNotFoundException e) {
        log.warn("해당 매치를 찾을 수 없습니다. {}", e.getMessage());

        return ResponseEntity.status(404)
            .body(BaseResponse.of(StatusType.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(InvalidMatchUpdateException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidMatchUpdateException(InvalidMatchUpdateException e) {
        log.warn("해당 매치의 상태를 변경할 수 없습니다. {}", e.getMessage());

        return ResponseEntity.badRequest()
            .body(BaseResponse.of(StatusType.BAD_REQUEST, e.getMessage()));
    }
}
