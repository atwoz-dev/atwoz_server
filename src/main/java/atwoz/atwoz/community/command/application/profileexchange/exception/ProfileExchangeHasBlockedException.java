package atwoz.atwoz.community.command.application.profileexchange.exception;

public class ProfileExchangeHasBlockedException extends RuntimeException {
    public ProfileExchangeHasBlockedException() {
        super("프로필 교환 요청자 또는 응답자가 차단한 회원입니다.");
    }
}
