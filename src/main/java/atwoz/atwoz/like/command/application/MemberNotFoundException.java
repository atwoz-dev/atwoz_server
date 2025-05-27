package atwoz.atwoz.like.command.application;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(long memberId) {
        super("좋아요 수신자가 존재하지 않습니다. memberId: " + memberId);
    }
}