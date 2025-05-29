package atwoz.atwoz.community.command.application.profileexchange.exception;

public class ProfileExchangeNotFoundException extends RuntimeException {
    public ProfileExchangeNotFoundException() {
        super("해당 프로필 교환을 찾을 수 없습니다.");
    }
}
