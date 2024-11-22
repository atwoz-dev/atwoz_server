package atwoz.atwoz.admin.domain.admin;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String password) {
        super("비밀번호는 10자 이상 20자 이하여야하며, 문자, 숫자, 특수문자를 포함해야 합니다: " + password);
    }
}
