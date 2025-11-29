package deepple.deepple.community.command.application.profileexchange.exception;

public class ProfileExchangeAlreadyExistsException extends RuntimeException {
    public ProfileExchangeAlreadyExistsException() {
        super("이미 해당 프로필 교환 신청이 존재합니다.");
    }
}
