package awtoz.awtoz.member.exception.auth;

public class SignatureInvalidException extends RuntimeException {
    public SignatureInvalidException() {
        super("서명이 유효하지 않습니다.");
    }
}
