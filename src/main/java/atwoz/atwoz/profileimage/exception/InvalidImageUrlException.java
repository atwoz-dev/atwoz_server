package atwoz.atwoz.profileimage.exception;


public class InvalidImageUrlException extends RuntimeException {
    public InvalidImageUrlException() {
        super("유효하지 않은 이미지 URL 주소입니다.");
    }
}
