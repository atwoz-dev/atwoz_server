package atwoz.atwoz.profileimage.exception;

public class InvalidImageFileException extends RuntimeException {
    public InvalidImageFileException() {
        super("해당 파일은 이미지가 아닙니다.");
    }
}
