package deepple.deepple.admin.command.domain.admin.exception;

public class InvalidNameException extends RuntimeException {
    public InvalidNameException(String name) {
        super("이름은 문자와 숫자만 포함해야하며, 최대 10자까지 설정 가능합니다: " + name);
    }
}
