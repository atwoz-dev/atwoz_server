package atwoz.atwoz.admin.command.application.admin.exception;

public class AdminNotFoundException extends RuntimeException {
    public AdminNotFoundException() {
        super("존재하지 않는 관리자입니다.");
    }
}
