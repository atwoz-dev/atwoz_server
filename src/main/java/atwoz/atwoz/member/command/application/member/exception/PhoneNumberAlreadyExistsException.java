package atwoz.atwoz.member.command.application.member.exception;

public class PhoneNumberAlreadyExistsException extends RuntimeException {
    public PhoneNumberAlreadyExistsException() {
        super("해당 번호를 사용하는 유저가 존재합니다. ");
    }
}
