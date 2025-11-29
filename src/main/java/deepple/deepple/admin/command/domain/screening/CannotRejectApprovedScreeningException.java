package deepple.deepple.admin.command.domain.screening;

public class CannotRejectApprovedScreeningException extends RuntimeException {
    public CannotRejectApprovedScreeningException() {
        super("이미 승인된 심사는 반려할 수 없습니다.");
    }
}
