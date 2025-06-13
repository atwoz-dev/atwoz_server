package atwoz.atwoz.member.command.domain.introduction.exception;

public class InvalidIntroductionMemberIdException extends RuntimeException {
    public InvalidIntroductionMemberIdException(long memberId, long introducedMemberId) {
        super("잘못된 소개 대상 멤버 아이디입니다. memberId: " + memberId + ", introducedMemberId: " + introducedMemberId);
    }
}
