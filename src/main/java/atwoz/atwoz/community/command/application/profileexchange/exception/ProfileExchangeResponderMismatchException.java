package atwoz.atwoz.community.command.application.profileexchange.exception;

public class ProfileExchangeResponderMismatchException extends RuntimeException {
    public ProfileExchangeResponderMismatchException() {
        super("해당 프로필 교환 요청의 응답 대상이 아닙니다.");
    }
}
