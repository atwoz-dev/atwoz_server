package atwoz.atwoz.member.command.infra.profileImage.exception;

import com.amazonaws.AmazonServiceException;

public class S3AmazonException extends RuntimeException {
    private final AmazonServiceException exception;

    public S3AmazonException(AmazonServiceException amazonServiceException) {
        super("S3 서비스에 문제가 발생하였습니다.");
        this.exception = amazonServiceException;
    }

    public AmazonServiceException getException() {
        return exception;
    }
}
