package atwoz.atwoz.payment.command.infra.order;

import atwoz.atwoz.payment.command.infra.order.exception.AppStoreClientException;
import atwoz.atwoz.payment.command.infra.order.exception.InvalidAppReceiptException;
import atwoz.atwoz.payment.command.infra.order.exception.InvalidTransactionIdException;
import com.apple.itunes.storekit.migration.ReceiptUtility;
import com.apple.itunes.storekit.model.JWSTransactionDecodedPayload;
import com.apple.itunes.storekit.verification.SignedDataVerifier;
import com.apple.itunes.storekit.verification.VerificationException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AppStoreClient {

    private final AppStoreFeignClient feignClient;
    private final AppStoreTokenService appStoreTokenService;
    private final ReceiptUtility receiptUtil;
    private final SignedDataVerifier signedDataVerifier;

    @Retry(name = AppStoreQueryResilienceConfig.RETRY_POLICY_NAME, fallbackMethod = "getTransactionDecodedPayloadFallback")
    @CircuitBreaker(name = AppStoreQueryResilienceConfig.CIRCUIT_BREAKER_POLICY_NAME, fallbackMethod = "getTransactionDecodedPayloadFallback")
    public JWSTransactionDecodedPayload getTransactionDecodedPayload(@NonNull String appReceipt) {
        String transactionId = extractTransactionIdFromReceipt(appReceipt);
        AppStoreTransactionResponse response = fetchTransactionFromAppStore(transactionId);
        return decodeTransactionPayload(response);
    }

    private String extractTransactionIdFromReceipt(String appReceipt) {
        try {
            String transactionId = receiptUtil.extractTransactionIdFromAppReceipt(appReceipt);
            if (transactionId == null) {
                throw new InvalidAppReceiptException("앱 영수증에 TransactionId가 없습니다.");
            }
            return transactionId;
        } catch (IllegalArgumentException exception) {
            throw new InvalidAppReceiptException(exception);
        } catch (IOException exception) {
            throw new AppStoreClientException(exception);
        }
    }

    private AppStoreTransactionResponse fetchTransactionFromAppStore(String transactionId) {
        try {
            String bearerToken = appStoreTokenService.generateToken();
            return feignClient.getTransactionInfo(transactionId, bearerToken);
        } catch (FeignException exception) {
            handleTokenExpirationIfNeeded(exception);
            throw exception;
        }
    }

    private void handleTokenExpirationIfNeeded(FeignException exception) {
        boolean isUnauthorized = exception.status() == 401;
        if (isUnauthorized) {
            appStoreTokenService.forceRefreshToken();
        }
    }


    private JWSTransactionDecodedPayload decodeTransactionPayload(AppStoreTransactionResponse response) {
        String signedTransactionInfo = response.getSignedTransactionInfo();
        try {
            return signedDataVerifier.verifyAndDecodeTransaction(signedTransactionInfo);
        } catch (VerificationException exception) {
            throw new AppStoreClientException(exception);
        }
    }

    public JWSTransactionDecodedPayload getTransactionDecodedPayloadFallback(String appReceipt, Exception exception) {
        if (exception instanceof FeignException feignEx) {
            int statusCode = feignEx.status();
            if (statusCode == 400 || statusCode == 404) {
                throw new InvalidTransactionIdException(feignEx);
            }
            throw new AppStoreClientException(feignEx);
        }
        throw new AppStoreClientException(exception);
    }
}
