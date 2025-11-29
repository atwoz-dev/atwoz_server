package deepple.deepple.community.command.application.profileexchange.exception;

public class ProfileExchangeResponderNotActiveException extends RuntimeException {
    public ProfileExchangeResponderNotActiveException() {
        super("프로필 교환 요청을 받은 사용자가 ACTIVE 상태가 아닙니다.");
    }
}
