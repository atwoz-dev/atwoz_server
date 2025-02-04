package atwoz.atwoz.member.command.infra.profileImage.exception;

import com.amazonaws.SdkClientException;

public class S3ClientException extends RuntimeException {
    private final SdkClientException exception;

    public S3ClientException(SdkClientException sdkClientException) {
        super("클라이언트 에러로 인해, 파일 업로드에 실패하였습니다.");
        this.exception = sdkClientException;
    }

    public SdkClientException getException() {
        return this.exception;
    }
}
