package atwoz.atwoz.payment.command.infra.order;

import atwoz.atwoz.payment.command.infra.order.exception.AppStoreClientException;
import atwoz.atwoz.payment.command.infra.order.exception.InvalidAppReceiptException;
import atwoz.atwoz.payment.command.infra.order.exception.InvalidTransactionIdException;
import com.apple.itunes.storekit.client.APIException;
import com.apple.itunes.storekit.client.AppStoreServerAPIClient;
import com.apple.itunes.storekit.migration.ReceiptUtility;
import com.apple.itunes.storekit.model.JWSTransactionDecodedPayload;
import com.apple.itunes.storekit.model.TransactionInfoResponse;
import com.apple.itunes.storekit.verification.SignedDataVerifier;
import com.apple.itunes.storekit.verification.VerificationException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AppStoreClient {

    private final AppStoreServerAPIClient client;
    private final ReceiptUtility receiptUtil;
    private final SignedDataVerifier signedDataVerifier;

    public JWSTransactionDecodedPayload getTransactionDecodedPayload(@NonNull String appReceipt) {
        try {
            String transactionId = getTransactionId(appReceipt);
            TransactionInfoResponse transactionInfoResponse = client.getTransactionInfo(transactionId);
            return getPayload(transactionInfoResponse);
        } catch (APIException e) {
            handleAPIException(e);
            throw new AppStoreClientException(e);
        } catch (IOException e) {
            throw new AppStoreClientException(e);
        }
    }

    private String getTransactionId(String appReceipt) {
        try {
            final String transactionId = receiptUtil.extractTransactionIdFromAppReceipt(appReceipt);
            if (transactionId == null) {
                throw new InvalidAppReceiptException("앱 영수증에 TransactionId가 없습니다.");
            }
            return transactionId;
        } catch (IllegalArgumentException e) {
            throw new InvalidAppReceiptException(e);
        } catch (IOException e) {
            throw new AppStoreClientException(e);
        }
    }

    private JWSTransactionDecodedPayload getPayload(TransactionInfoResponse transactionInfoResponse) {
        String signedTransactionInfo = transactionInfoResponse.getSignedTransactionInfo();
        try {
            return signedDataVerifier.verifyAndDecodeTransaction(signedTransactionInfo);
        } catch (VerificationException e) {
            throw new AppStoreClientException(e);
        }
    }


    private void handleAPIException(APIException e) {
        int statusCode = e.getHttpStatusCode();
        if (statusCode == 400 || statusCode == 404) {
            throw new InvalidTransactionIdException(e);
        }
    }
}