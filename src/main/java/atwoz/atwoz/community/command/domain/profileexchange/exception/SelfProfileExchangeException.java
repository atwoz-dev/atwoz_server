package atwoz.atwoz.community.command.domain.profileexchange.exception;

public class SelfProfileExchangeException extends RuntimeException {
    public SelfProfileExchangeException() {
        super("자기 자신과의 프로필 교환을 신청할 수 없습니다.");
    }
}
