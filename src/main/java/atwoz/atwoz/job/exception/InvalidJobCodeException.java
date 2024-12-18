package atwoz.atwoz.job.exception;

public class InvalidJobCodeException extends RuntimeException {
    public InvalidJobCodeException() {
        super("유효하지 않은 직업 코드입니다.");
    }
}
