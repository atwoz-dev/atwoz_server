package deepple.deepple.admin.command.application.suspension;

public class InvalidSuspensionStatusException extends RuntimeException {
    public InvalidSuspensionStatusException(String status) {
        super(status + "는 유효하지 않은 정지 상태입니다.");
    }
}
