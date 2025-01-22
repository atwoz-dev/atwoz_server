package atwoz.atwoz.member.domain.member.exception;

public class InvalidHobbyIdException extends RuntimeException {
    public InvalidHobbyIdException() {
        super("유효하지 않은 취미 아이디입니다.");
    }
}
