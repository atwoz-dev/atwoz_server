package atwoz.atwoz.member.presentation.member;

import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.member.command.application.member.exception.*;
import atwoz.atwoz.member.command.domain.member.exception.MemberNotActiveException;
import atwoz.atwoz.member.query.member.application.exception.ProfileAccessDeniedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MemberExceptionHandler {

    @ExceptionHandler(PermanentlySuspendedMemberException.class)
    public ResponseEntity<BaseResponse<Void>> handlePermanentlySuspendedMemberException(
        PermanentlySuspendedMemberException e
    ) {
        log.warn("멤버 로그인에 실패하였습니다. {}", e.getMessage());

        return ResponseEntity.status(403)
            .body(BaseResponse.from(StatusType.FORBIDDEN));
    }

    @ExceptionHandler(TemporarilySuspendedMemberException.class)
    public ResponseEntity<BaseResponse<Void>> handleTemporarilySuspendedMemberException(
        TemporarilySuspendedMemberException e
    ) {
        log.warn("멤버 로그인에 실패하였습니다. {}", e.getMessage());

        return ResponseEntity.status(403)
            .body(BaseResponse.from(StatusType.TEMPORARILY_FORBIDDEN));
    }

    @ExceptionHandler(MemberDeletedException.class)
    public ResponseEntity<BaseResponse<Void>> handleMemberDeletedException(MemberDeletedException e) {
        log.warn("멤버 로그인에 실패하였습니다. {}", e.getMessage());

        return ResponseEntity.status(403)
            .body(BaseResponse.from(StatusType.DELETED_TARGET));
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleMemberNotFoundException(MemberNotFoundException e) {
        log.warn("멤버 조회에 실패하였습니다. {}", e.getMessage());

        return ResponseEntity.status(404)
            .body(BaseResponse.from(StatusType.NOT_FOUND));
    }

    @ExceptionHandler(PhoneNumberAlreadyExistsException.class)
    public ResponseEntity<BaseResponse<Void>> handlePhoneNumberAlreadyExistsException(
        PhoneNumberAlreadyExistsException e) {
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

    @ExceptionHandler(PrimaryContactTypeSettingNeededException.class)
    public ResponseEntity<BaseResponse<Void>> handlePrimaryContactTypeSettingNeededException(
        PrimaryContactTypeSettingNeededException e) {
        log.warn("매치 요청/응답에 실패하였습니다. {}", e.getMessage());

        return ResponseEntity.badRequest()
            .body(BaseResponse.from(StatusType.BAD_REQUEST));
    }

    @ExceptionHandler(ProfileAccessDeniedException.class)
    public ResponseEntity<BaseResponse<Void>> handleProfileAccessDeniedException(
        ProfileAccessDeniedException e) {
        log.warn("프로필 조회에 실패하였습니다. {}", e.getMessage());

        return ResponseEntity.status(403)
            .body(BaseResponse.from(StatusType.FORBIDDEN));
    }

    @ExceptionHandler(CodeNotExistsException.class)
    public ResponseEntity<BaseResponse<Void>> handleCodeNotExistsException(CodeNotExistsException e) {
        log.warn("코드 인증에 실패하였습니다. {}", e.getMessage());

        return ResponseEntity.status(404)
            .body(BaseResponse.from(StatusType.NOT_FOUND));
    }

    @ExceptionHandler(CodeNotMatchException.class)
    public ResponseEntity<BaseResponse<Void>> handleCodeNotMatchException(CodeNotMatchException e) {
        log.warn("코드가 일치하지 않습니다. {}", e.getMessage());

        return ResponseEntity.badRequest()
            .body(BaseResponse.from(StatusType.BAD_REQUEST));
    }

    @ExceptionHandler(MemberNotActiveException.class)
    public ResponseEntity<BaseResponse<Void>> handleMemberNotActiveException(MemberNotActiveException e) {
        log.warn("회원의 상태가 부적절합니다. {}", e.getMessage());

        return ResponseEntity.badRequest()
            .body(BaseResponse.from(StatusType.DORMANT_STATUS));
    }

    @ExceptionHandler(MemberWaitingStatusException.class)
    public ResponseEntity<BaseResponse<Void>> handleMemberWaitingStatusException(MemberWaitingStatusException e) {
        log.warn("로그인에 실패하였습니다.", e.getMessage());

        return ResponseEntity.status(403)
            .body(BaseResponse.from(StatusType.WAITING_STATUS));
    }
}
