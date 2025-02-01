package atwoz.atwoz.member.command.domain.member.exception;

public class InvalidNicknameException extends RuntimeException {
    public InvalidNicknameException() {
       super("해당 닉네임은 사용할 수 없습니다.");
    }
}
