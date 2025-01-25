package atwoz.atwoz.admin.command.domain.memberscreening;

public class CannotRejectApprovedScreeningException extends RuntimeException {
    public CannotRejectApprovedScreeningException() {
        super("이미 승인된 심사는 반려할 수 없습니다.");
    }
}
