package deepple.deepple.member.command.application.profileImage.exception;

public class FileUploadFailException extends RuntimeException {

    public FileUploadFailException(Exception e) {
        super("파일 업로드에 실패하였습니다.");
    }
}
