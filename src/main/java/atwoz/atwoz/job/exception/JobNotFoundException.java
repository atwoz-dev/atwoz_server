package atwoz.atwoz.job.exception;

public class JobNotFoundException extends RuntimeException {
    public JobNotFoundException() {
        super("해당 직업을 찾을 수 없습니다.");
    }
}
