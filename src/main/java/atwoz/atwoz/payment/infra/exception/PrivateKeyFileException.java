package atwoz.atwoz.payment.infra.exception;

public class PrivateKeyFileException extends RuntimeException {
    public PrivateKeyFileException(Exception e) {
        super("private key file에서 문제가 발생했습니다.", e);
    }
}
