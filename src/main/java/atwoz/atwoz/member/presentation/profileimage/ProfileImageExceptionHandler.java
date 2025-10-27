package atwoz.atwoz.member.presentation.profileimage;

import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.member.command.application.profileImage.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ProfileImageExceptionHandler {

    @ExceptionHandler(InvalidPrimaryProfileImageCountException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidPrimaryProfileImageCountException(
        InvalidPrimaryProfileImageCountException e) {
        log.warn("이미지 업로드에 실패하였습니다. {}", e.getMessage());

        return ResponseEntity.badRequest()
            .body(BaseResponse.of(StatusType.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(ProfileImageMemberIdMismatchException.class)
    public ResponseEntity<BaseResponse<Void>> handleProfileImageMemberIdMismatchException(
        ProfileImageMemberIdMismatchException e) {
        log.warn("프로필 이미지가 유저와 일치하지 않습니다. {}", e.getMessage());

        return ResponseEntity.badRequest()
            .body(BaseResponse.of(StatusType.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(ProfileImageNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleProfileImageNotFoundException(ProfileImageNotFoundException e) {
        log.warn("이미지 조회에 실패하였습니다. {}", e.getMessage());

        return ResponseEntity.status(404)
            .body(BaseResponse.of(StatusType.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(FileUploadFailException.class)
    public ResponseEntity<BaseResponse<Void>> handleFileUploadFailException(FileUploadFailException e) {
        log.error("파일 업로드에 실패하였습니다. {}", e.getMessage());

        return ResponseEntity.status(500)
            .body(BaseResponse.from(StatusType.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(EmptyImageUploadException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidImageFileException(EmptyImageUploadException e) {
        log.warn("이미지 업로드에 실패하였습니다. {}", e.getMessage());

        return ResponseEntity.badRequest()
            .body(BaseResponse.of(StatusType.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(DuplicateProfileImageOrderException.class)
    public ResponseEntity<BaseResponse<Void>> handleDuplicateProfileImageOrderException(
        DuplicateProfileImageOrderException e) {
        log.warn("이미지 업로드에 실패하였습니다. {}", e.getMessage());

        return ResponseEntity.badRequest()
            .body(BaseResponse.of(StatusType.BAD_REQUEST, e.getMessage()));
    }


}
