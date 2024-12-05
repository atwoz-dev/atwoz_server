package atwoz.atwoz.profileimage.exception;

public class FIleUploadFailException extends RuntimeException {
    public FIleUploadFailException() {
        super("파일 업로드에 실패하였습니다.");
    }
}
