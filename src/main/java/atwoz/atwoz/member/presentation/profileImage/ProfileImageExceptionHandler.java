package atwoz.atwoz.member.presentation.profileImage;

import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.member.command.application.profileImage.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ProfileImageExceptionHandler {

    @ExceptionHandler(InvalidPrimaryProfileImageCountException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidPrimaryProfileImageCountException(InvalidPrimaryProfileImageCountException e) {
        log.warn("이미지 업로드에 실패하였습니다. {}", e.getMessage());

        return ResponseEntity.badRequest()
                .body(BaseResponse.from(StatusType.BAD_REQUEST));
    }

    @ExceptionHandler(PrimaryImageAlreadyExistsException.class)
    public ResponseEntity<BaseResponse<Void>> handlePrimaryImageAlreadyExistsException(PrimaryImageAlreadyExistsException e) {
        log.warn("대표 이미지 설정에 실패하였습니다. {}", e.getMessage());

        return ResponseEntity.badRequest()
                .body(BaseResponse.from(StatusType.BAD_REQUEST));
    }

    @ExceptionHandler(ProfileImageMemberIdMismatchException.class)
    public ResponseEntity<BaseResponse<Void>> handleProfileImageMemberIdMismatchException(ProfileImageMemberIdMismatchException e) {
        log.warn("프로필 이미지가 유저와 일치하지 않습니다. {}", e.getMessage());

        return ResponseEntity.badRequest()
                .body(BaseResponse.from(StatusType.BAD_REQUEST));
    }

    @ExceptionHandler(ProfileImageNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleProfileImageNotFoundException(ProfileImageNotFoundException e) {
        log.warn("이미지 조회에 실패하였습니다. {}", e.getMessage());

        return ResponseEntity.status(404)
                .body(BaseResponse.from(StatusType.NOT_FOUND));
    }

    @ExceptionHandler(FileUploadFailException.class)
    public ResponseEntity<BaseResponse<Void>> handleFileUploadFailException(FileUploadFailException e) {
        log.warn("파일 업로드에 실패하였습니다. {}", e.getMessage());

        return ResponseEntity.status(500)
                .body(BaseResponse.from(StatusType.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(InvalidImageFileException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidImageFileException(InvalidImageFileException e) {
        log.warn("이미지 업로드에 실패하였습니다. {}", e.getMessage());

        return ResponseEntity.badRequest()
                .body(BaseResponse.from(StatusType.BAD_REQUEST));
    }

    @ExceptionHandler(InvalidIsPrimaryException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidIsPrimaryException(InvalidIsPrimaryException e) {
        log.warn("이미지 업로드에 실패하였습니다.", e.getMessage());

        return ResponseEntity.badRequest()
                .body(BaseResponse.from(StatusType.BAD_REQUEST));
    }

    @ExceptionHandler(InvalidMemberIdException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidMemberIdException(InvalidMemberIdException e) {
        log.warn("잘못된 유저 ID 입니다. {}", e.getMessage());

        return ResponseEntity.badRequest()
                .body(BaseResponse.from(StatusType.BAD_REQUEST));
    }
}
