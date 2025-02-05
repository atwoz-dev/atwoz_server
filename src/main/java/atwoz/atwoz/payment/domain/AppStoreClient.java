package atwoz.atwoz.payment.domain;

import com.apple.itunes.storekit.model.TransactionInfoResponse;

public interface AppStoreClient {
    TransactionInfoResponse getTransactionInfo(String transactionId);
}
