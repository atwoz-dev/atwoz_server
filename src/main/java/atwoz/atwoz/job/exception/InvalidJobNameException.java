package atwoz.atwoz.job.exception;

public class InvalidJobNameException extends RuntimeException {
    public InvalidJobNameException() {
        super("유효하지 않은 직업명입니다.");
    }
}
