package atwoz.atwoz.admin.command.domain.admin.exception;

public class InvalidPhoneNumberException extends RuntimeException {
    public InvalidPhoneNumberException(String phoneNumber) {
        super("유효하지 않은 전화번호 형식입니다: " + phoneNumber);
    }
}
