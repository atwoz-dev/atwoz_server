package atwoz.atwoz.match.presentation;

import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.match.command.application.match.exception.ExistsMatchException;
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
                .body(BaseResponse.from(StatusType.BAD_REQUEST));
    }
}
