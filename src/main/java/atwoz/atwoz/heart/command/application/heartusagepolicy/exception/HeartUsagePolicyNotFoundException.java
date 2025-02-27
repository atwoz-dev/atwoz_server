package atwoz.atwoz.heart.command.application.heartusagepolicy.exception;

public class HeartUsagePolicyNotFoundException extends RuntimeException {
    public HeartUsagePolicyNotFoundException() {
        super("해당하는 하트 사용 정책이 존재하지 않습니다.");
    }
}
