package atwoz.atwoz.admin.command.application.memberscreening;

public class InvalidRejectionReasonException extends RuntimeException {
    public InvalidRejectionReasonException(String rejectionReason) {
        super(rejectionReason + "은 유효하지 않은 반려 사유입니다.");
    }
}
