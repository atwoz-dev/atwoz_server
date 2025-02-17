package atwoz.atwoz.admin.command.domain.screening;

public class ScreeningNotFoundException extends RuntimeException {
    public ScreeningNotFoundException(Long memberId) {
        super("멤버(id: " + memberId + ")에 대한 심사를 찾을 수 없습니다.");
    }
}
