package atwoz.atwoz.payment.infra;

import atwoz.atwoz.payment.command.infra.AppStoreClient;
import atwoz.atwoz.payment.command.infra.exception.AppStoreClientException;
import atwoz.atwoz.payment.command.infra.exception.InvalidTransactionIdException;
import com.apple.itunes.storekit.client.APIException;
import com.apple.itunes.storekit.client.AppStoreServerAPIClient;
import com.apple.itunes.storekit.model.TransactionInfoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppStoreClientTest {

    @InjectMocks
    private AppStoreClient appStoreClient;

    @Mock
    private AppStoreServerAPIClient appStoreServerAPIClient;

    @Test
    @DisplayName("API 요청이 성공하면 TransactionInfoResponse를 반환한다")
    public void successWhenApiRequestResponseStatusIsOk() throws Exception {
        // given
        String transactionId = "transactionId";
        String signedTransactionInfo = "signedTransactionInfo";

        TransactionInfoResponse expectedResponse = new TransactionInfoResponse();
        expectedResponse.signedTransactionInfo(signedTransactionInfo);

        when(appStoreServerAPIClient.getTransactionInfo(transactionId)).thenReturn(expectedResponse);

        // when
        TransactionInfoResponse actualResponse = appStoreClient.getTransactionInfo(transactionId);

        // then
        assertThat(actualResponse.getSignedTransactionInfo())
                .isEqualTo(signedTransactionInfo);
    }

    @Test
    @DisplayName("API 요청이 400으로 실패하면 InvalidTransactionIdException 던진다")
    public void testGetTransactionInfoFailure() throws Exception {
        // given
        String transactionId = "transactionId";
        int statusCode = 400;

        when(appStoreServerAPIClient.getTransactionInfo(transactionId)).thenThrow(new APIException(statusCode));

        // when && then
        assertThatThrownBy(() -> appStoreClient.getTransactionInfo(transactionId))
                .isInstanceOf(InvalidTransactionIdException.class);

        verify(appStoreServerAPIClient, times(1)).getTransactionInfo(transactionId);
    }

    @Test
    @DisplayName("API 요청이 404로 실패하면 InvalidTransactionIdException 던진다")
    public void throwInvalidTransactionIdExceptionWhenApiRequestFailWith404() throws Exception {
        // given
        String transactionId = "transactionId";
        int statusCode = 404;

        when(appStoreServerAPIClient.getTransactionInfo(transactionId)).thenThrow(new APIException(statusCode));

        // when && then
        assertThatThrownBy(() -> appStoreClient.getTransactionInfo(transactionId))
                .isInstanceOf(InvalidTransactionIdException.class);

        verify(appStoreServerAPIClient, times(1)).getTransactionInfo(transactionId);
    }

    @Test
    @DisplayName("API 요청이 500으로 실패하면 AppStoreClientException 던진다")
    public void throwAppStoreClientExceptionWhenApiRequestFailWith500() throws Exception {
        // given
        String transactionId = "transactionId";
        int statusCode = 500;

        when(appStoreServerAPIClient.getTransactionInfo(transactionId)).thenThrow(new APIException(statusCode));

        // when && then
        assertThatThrownBy(() -> appStoreClient.getTransactionInfo(transactionId))
                .isInstanceOf(AppStoreClientException.class);

        verify(appStoreServerAPIClient, times(1)).getTransactionInfo(transactionId);
    }

    @Test
    @DisplayName("API 요청이 401으로 실패하면 AppStoreClientException 던진다")
    public void throwAppStoreClientExceptionWhenApiRequestFailWith401() throws Exception {
        // given
        String transactionId = "transactionId";
        int statusCode = 401;

        when(appStoreServerAPIClient.getTransactionInfo(transactionId)).thenThrow(new APIException(statusCode));

        // when && then
        assertThatThrownBy(() -> appStoreClient.getTransactionInfo(transactionId))
                .isInstanceOf(AppStoreClientException.class);

        verify(appStoreServerAPIClient, times(1)).getTransactionInfo(transactionId);
    }

    @Test
    @DisplayName("API 요청이 429으로 실패하면 AppStoreClientException 던진다")
    public void throwAppStoreClientExceptionWhenApiRequestFailWith429() throws Exception {
        // given
        String transactionId = "transactionId";
        int statusCode = 429;

        when(appStoreServerAPIClient.getTransactionInfo(transactionId)).thenThrow(new APIException(statusCode));

        // when && then
        assertThatThrownBy(() -> appStoreClient.getTransactionInfo(transactionId))
                .isInstanceOf(AppStoreClientException.class);

        verify(appStoreServerAPIClient, times(1)).getTransactionInfo(transactionId);
    }
}