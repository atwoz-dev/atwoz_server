package atwoz.atwoz.payment.command.infra.exception;

public class AppStoreClientException extends RuntimeException {
    public AppStoreClientException(Exception e) {
        super("App Store API 요청 중 오류가 발생했습니다.", e);
    }
}
