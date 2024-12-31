package atwoz.atwoz.member.exception;

public class InvalidMemberEnumValueException extends RuntimeException {
    public InvalidMemberEnumValueException(String message) {
        super("유효하지 않은 Enum 값 입니다 : " + message);
    }
}
