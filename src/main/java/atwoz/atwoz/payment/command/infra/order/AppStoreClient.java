package atwoz.atwoz.payment.command.infra.order;

import atwoz.atwoz.payment.command.infra.order.exception.AppStoreClientException;
import atwoz.atwoz.payment.command.infra.order.exception.InvalidAppReceiptException;
import atwoz.atwoz.payment.command.infra.order.exception.InvalidTransactionIdException;
import com.apple.itunes.storekit.client.APIException;
import com.apple.itunes.storekit.client.AppStoreServerAPIClient;
import com.apple.itunes.storekit.migration.ReceiptUtility;
import com.apple.itunes.storekit.model.TransactionInfoResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppStoreClient {

    private final AppStoreServerAPIClient client;
    private final ReceiptUtility receiptUtil;

    public TransactionInfoResponse getTransactionInfo(@NonNull String appReceipt) {
        try {
            String transactionId = getTransactionId(appReceipt);
            return client.getTransactionInfo(transactionId);
        } catch (APIException e) {
            handleAPIException(e);
            throw new AppStoreClientException(e);
        } catch (Exception e) {
            throw new AppStoreClientException(e);
        }
    }

    private String getTransactionId(String appReceipt) {
        try {
            return receiptUtil.extractTransactionIdFromAppReceipt(appReceipt);
        } catch (Exception e) {
            throw new InvalidAppReceiptException(e);
        }
    }

    private void handleAPIException(APIException e) {
        int statusCode = e.getHttpStatusCode();
        if (statusCode == 400 || statusCode == 404) {
            throw new InvalidTransactionIdException(e);
        }
    }
}