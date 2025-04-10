package atwoz.atwoz.member.presentation.member;

import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.member.command.application.member.exception.BannedMemberException;
import atwoz.atwoz.member.command.application.member.exception.KakaoIdAlreadyExistsException;
import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.command.application.member.exception.PhoneNumberAlreadyExistsException;
import atwoz.atwoz.member.command.domain.member.exception.InvalidHobbyIdException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class MemberExceptionHandler {

    @ExceptionHandler(BannedMemberException.class)
    public ResponseEntity<BaseResponse<Void>> handleBannedMemberException(BannedMemberException e) {
        log.warn("멤버 로그인에 실패하였습니다. {}", e.getMessage());

        return ResponseEntity.status(401)
                .body(BaseResponse.from(StatusType.UNAUTHORIZED));
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleMemberNotFoundException(MemberNotFoundException e) {
        log.warn("멤버 조회에 실패하였습니다. {}", e.getMessage());

        return ResponseEntity.status(404)
                .body(BaseResponse.from(StatusType.NOT_FOUND));
    }

    @ExceptionHandler(PhoneNumberAlreadyExistsException.class)
    public ResponseEntity<BaseResponse<Void>> handlePhoneNumberAlreadyExistsException(PhoneNumberAlreadyExistsException e) {
        log.warn("휴대폰 번호 변경에 실패하였습니다. {}", e.getMessage());

        return ResponseEntity.badRequest()
                .body(BaseResponse.from(StatusType.BAD_REQUEST));
    }

    @ExceptionHandler(KakaoIdAlreadyExistsException.class)
    public ResponseEntity<BaseResponse<Void>> handleKakaoIdAlreadyExistsException(KakaoIdAlreadyExistsException e) {
        log.warn("카카오 아이디 변경에 실패하였습니다. {}", e.getMessage());

        return ResponseEntity.badRequest()
                .body(BaseResponse.from(StatusType.BAD_REQUEST));
    }

    @ExceptionHandler(InvalidHobbyIdException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidHobbyIdException(InvalidHobbyIdException e) {
        log.warn("유효하지 않은 취미 아이디입니다. {}", e.getMessage());

        return ResponseEntity.badRequest()
                .body(BaseResponse.from(StatusType.BAD_REQUEST));
    }
}
