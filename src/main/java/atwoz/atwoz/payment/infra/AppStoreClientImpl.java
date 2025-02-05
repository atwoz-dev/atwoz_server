package atwoz.atwoz.payment.infra;

import atwoz.atwoz.payment.domain.AppStoreClient;
import atwoz.atwoz.payment.infra.exception.AppStoreClientException;
import atwoz.atwoz.payment.infra.exception.InvalidTransactionIdException;
import com.apple.itunes.storekit.client.APIException;
import com.apple.itunes.storekit.client.AppStoreServerAPIClient;
import com.apple.itunes.storekit.model.TransactionInfoResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppStoreClientImpl implements AppStoreClient {

    private final AppStoreServerAPIClient client;

    @Override
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