package atwoz.atwoz.community.command.application.profileexchange.exception;

public class ProfileExchangeAlreadyExists extends RuntimeException {
    public ProfileExchangeAlreadyExists() {
        super("이미 해당 프로필 교환 신청이 존재합니다.");
    }
}
