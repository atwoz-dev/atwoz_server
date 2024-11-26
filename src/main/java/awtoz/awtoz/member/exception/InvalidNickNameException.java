package awtoz.awtoz.member.exception;

public class InvalidNickNameException extends RuntimeException {
    public InvalidNickNameException() {
       super("해당 닉네임은 사용할 수 없습니다.");
    }
}
