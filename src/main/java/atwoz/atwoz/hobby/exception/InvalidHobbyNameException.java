package atwoz.atwoz.hobby.exception;

public class InvalidHobbyNameException extends RuntimeException {
    public InvalidHobbyNameException() {
        super("유효하지 않은 취미명입니다.");
    }
}