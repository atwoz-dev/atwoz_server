package atwoz.atwoz.common.domain;

public class InvalidPhoneNumberException extends RuntimeException {
    public InvalidPhoneNumberException(String phoneNumber) {
        super("유효하지 않은 전화번호 형식입니다: " + phoneNumber);
    }
}
