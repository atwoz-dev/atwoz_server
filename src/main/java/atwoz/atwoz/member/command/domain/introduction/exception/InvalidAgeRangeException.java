package atwoz.atwoz.member.command.domain.introduction.exception;

public class InvalidAgeRangeException extends RuntimeException {
    public InvalidAgeRangeException() {
        super("나이 범위가 유효하지 않습니다.");
    }
}
