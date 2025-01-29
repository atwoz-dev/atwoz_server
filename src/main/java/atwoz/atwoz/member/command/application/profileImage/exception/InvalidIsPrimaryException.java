package atwoz.atwoz.member.command.application.profileImage.exception;

public class InvalidIsPrimaryException extends RuntimeException {
    public InvalidIsPrimaryException() {
        super("대표이미지 컬럼에는 NULL이 들어갈 수 없습니다.");
    }
}
