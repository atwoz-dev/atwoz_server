package atwoz.atwoz.payment.command.infra;

import atwoz.atwoz.payment.command.infra.exception.AppStoreClientException;
import atwoz.atwoz.payment.command.infra.exception.InvalidTransactionIdException;
import com.apple.itunes.storekit.client.APIException;
import com.apple.itunes.storekit.client.AppStoreServerAPIClient;
import com.apple.itunes.storekit.model.TransactionInfoResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppStoreClient {

    private final AppStoreServerAPIClient client;

    public TransactionInfoResponse getTransactionInfo(@NonNull String transactionId) {
        try {
            return client.getTransactionInfo(transactionId);
        } catch (APIException e) {
            handleAPIException(e);
            throw new AppStoreClientException(e);
        } catch (Exception e) {
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