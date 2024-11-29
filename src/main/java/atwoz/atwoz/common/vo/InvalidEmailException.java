package atwoz.atwoz.common.vo;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String address) {
        super("유효하지 않은 이메일 주소 형식입니다: " + address);
    }
}
