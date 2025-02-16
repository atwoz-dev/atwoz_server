package atwoz.atwoz.member.command.application.profileImage.exception;

public class EmptyImageUploadException extends RuntimeException {
    public EmptyImageUploadException() {
        super("비어있는 파일을 업로드 할 수 없습니다.");
    }
}
