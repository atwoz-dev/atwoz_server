package atwoz.atwoz.admin.command.domain.admin.exception;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String password) {
        super("유효하지 않은 비밀번호입니다: " + password);
    }
}
