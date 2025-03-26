package atwoz.atwoz.member.command.domain.member.exception;

public class InvalidYearOfBirthException extends RuntimeException {
    public InvalidYearOfBirthException() {
        super("현재 나이가 20살 이상, 46살 이하이어야 합니다.");
    }
}
