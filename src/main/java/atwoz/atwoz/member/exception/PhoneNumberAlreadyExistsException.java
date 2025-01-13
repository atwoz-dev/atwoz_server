package atwoz.atwoz.member.exception;

public class PhoneNumberAlreadyExistsException extends RuntimeException {
    public PhoneNumberAlreadyExistsException() {
        super("해당 번호는 이미 사용중인 번호입니다.");
    }
}
