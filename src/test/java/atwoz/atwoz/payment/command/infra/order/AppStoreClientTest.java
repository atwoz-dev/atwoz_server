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
import com.apple.itunes.storekit.verification.VerificationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppStoreClientTest {

    @InjectMocks
    private AppStoreClient appStoreClient;

    @Mock
    private AppStoreServerAPIClient appStoreServerAPIClient;

    @Mock
    private ReceiptUtility receiptUtil;

    @Mock
    private SignedDataVerifier signedDataVerifier;

    @Test
    @DisplayName("API 요청이 성공하면 TransactionInfoResponse를 반환한다")
    void successWhenApiRequestResponseStatusIsOk() throws Exception {
        // given
        String appReceipt = "appReceipt";

        String transactionId = "transactionId";
        when(receiptUtil.extractTransactionIdFromAppReceipt(appReceipt)).thenReturn(transactionId);

        TransactionInfoResponse transactionInfoResponse = mock(TransactionInfoResponse.class);
        when(appStoreServerAPIClient.getTransactionInfo(transactionId)).thenReturn(transactionInfoResponse);

        String signedTransactionInfo = "signedTransactionInfo";
        when(transactionInfoResponse.getSignedTransactionInfo()).thenReturn(signedTransactionInfo);

        JWSTransactionDecodedPayload decodedPayload = mock(JWSTransactionDecodedPayload.class);
        when(signedDataVerifier.verifyAndDecodeTransaction(signedTransactionInfo)).thenReturn(decodedPayload);

        // when
        JWSTransactionDecodedPayload result = appStoreClient.getTransactionDecodedPayload(appReceipt);

        // then
        assertThat(result).isEqualTo(decodedPayload);
    }

    @Test
    @DisplayName("app receipt에서 transactionId를 추출할 때 IllegalArgumentException이 발생하면 InvalidAppReceiptException을 던진다")
    void testGetTransactionDecodedPayloadFailureWhenExtractTransactionIdFromAppReceipt() throws Exception {
        // given
        String appReceipt = "appReceipt";

        when(receiptUtil.extractTransactionIdFromAppReceipt(appReceipt)).thenThrow(
            new IllegalArgumentException("Invalid receipt"));

        // when && then
        assertThatThrownBy(() -> appStoreClient.getTransactionDecodedPayload(appReceipt))
            .isInstanceOf(InvalidAppReceiptException.class);
    }

    @Test
    @DisplayName("app receipt에서 transactionId를 추출할 때 IOException이 발생하면 AppStoreClientException을 던진다")
    void testGetTransactionDecodedPayloadFailureWhenExtractTransactionIdFromAppReceiptIOException()
        throws Exception {
        // given
        String appReceipt = "appReceipt";

        when(receiptUtil.extractTransactionIdFromAppReceipt(appReceipt)).thenThrow(new IOException("IO error"));

        // when && then
        assertThatThrownBy(() -> appStoreClient.getTransactionDecodedPayload(appReceipt))
            .isInstanceOf(AppStoreClientException.class);

        verify(appStoreServerAPIClient, never()).getTransactionInfo(any());
    }

    @Test
    @DisplayName("API 요청이 400으로 실패하면 InvalidTransactionIdException 던진다")
    void testGetTransactionDecodedPayloadFailure() throws Exception {
        // given
        String appReceipt = "appReceipt";
        String transactionId = "transactionId";
        when(receiptUtil.extractTransactionIdFromAppReceipt(appReceipt)).thenReturn(transactionId);

        int statusCode = 400;
        when(appStoreServerAPIClient.getTransactionInfo(transactionId)).thenThrow(new APIException(statusCode));

        // when && then
        assertThatThrownBy(() -> appStoreClient.getTransactionDecodedPayload(appReceipt))
            .isInstanceOf(InvalidTransactionIdException.class);
    }

    @Test
    @DisplayName("API 요청이 404로 실패하면 InvalidTransactionIdException 던진다")
    void throwInvalidTransactionIdExceptionWhenApiRequestFailWith404() throws Exception {
        // given
        String appReceipt = "appReceipt";
        String transactionId = "transactionId";
        when(receiptUtil.extractTransactionIdFromAppReceipt(appReceipt)).thenReturn(transactionId);

        int statusCode = 404;
        when(appStoreServerAPIClient.getTransactionInfo(transactionId)).thenThrow(new APIException(statusCode));

        // when && then
        assertThatThrownBy(() -> appStoreClient.getTransactionDecodedPayload(appReceipt))
            .isInstanceOf(InvalidTransactionIdException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {401, 429, 500})
    @DisplayName("API 요청이 401, 429, 500으로 실패하면 AppStoreClientException 던진다")
    void throwAppStoreClientExceptionWhenApiRequestFailWith401or429or500(int statusCode) throws Exception {
        // given
        String appReceipt = "appReceipt";
        String transactionId = "transactionId";
        when(receiptUtil.extractTransactionIdFromAppReceipt(appReceipt)).thenReturn(transactionId);
        when(appStoreServerAPIClient.getTransactionInfo(transactionId)).thenThrow(new APIException(statusCode));

        // when && then
        assertThatThrownBy(() -> appStoreClient.getTransactionDecodedPayload(appReceipt))
            .isInstanceOf(AppStoreClientException.class);
    }

    @Test
    @DisplayName("signedTransactionInfo를 verify하고 decode할 때 VerificationException이 발생하면 AppStoreClientException을 던진다")
    void testGetTransactionDecodedPayloadFailureWhenVerifyAndDecodeTransaction() throws Exception {
        // given
        String appReceipt = "appReceipt";
        String transactionId = "transactionId";
        when(receiptUtil.extractTransactionIdFromAppReceipt(appReceipt)).thenReturn(transactionId);

        TransactionInfoResponse transactionInfoResponse = mock(TransactionInfoResponse.class);
        when(appStoreServerAPIClient.getTransactionInfo(transactionId)).thenReturn(transactionInfoResponse);

        String signedTransactionInfo = "signedTransactionInfo";
        when(transactionInfoResponse.getSignedTransactionInfo()).thenReturn(signedTransactionInfo);

        when(signedDataVerifier.verifyAndDecodeTransaction(signedTransactionInfo))
            .thenThrow(new VerificationException(VerificationStatus.VERIFICATION_FAILURE));

        // when && then
        assertThatThrownBy(() -> appStoreClient.getTransactionDecodedPayload(appReceipt))
            .isInstanceOf(AppStoreClientException.class);
    }
}