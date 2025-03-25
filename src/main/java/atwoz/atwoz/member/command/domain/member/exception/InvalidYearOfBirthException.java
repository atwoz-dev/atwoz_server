package atwoz.atwoz.member.command.domain.member.exception;

public class InvalidYearOfBirthException extends RuntimeException {
    public InvalidYearOfBirthException() {
        super("20세 이상 46세 이하만 설정이 가능합니다.");
    }
}
