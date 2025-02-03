package atwoz.atwoz.admin.command.domain.memberscreening;

public class MemberScreeningNotFoundException extends RuntimeException {
    public MemberScreeningNotFoundException(Long memberId) {
        super("멤버(id: " + memberId + ") 에 대한 심사를 찾을 수 없습니다.");
    }
}
