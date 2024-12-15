package atwoz.atwoz.profileimage.exception;

public class PrimaryImageAlreadyExistsException extends RuntimeException {
    public PrimaryImageAlreadyExistsException() {
        super("대표이미지가 이미 존재합니다.");
    }
}
