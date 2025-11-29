package deepple.deepple.payment.command.infra.order;

import com.apple.itunes.storekit.migration.ReceiptUtility;
import com.apple.itunes.storekit.model.JWSTransactionDecodedPayload;
import com.apple.itunes.storekit.verification.SignedDataVerifier;
import com.apple.itunes.storekit.verification.VerificationException;
import deepple.deepple.payment.command.infra.order.exception.AppStoreClientException;
import deepple.deepple.payment.command.infra.order.exception.InvalidAppReceiptException;
import deepple.deepple.payment.command.infra.order.exception.InvalidTransactionIdException;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppStoreClient 단위 테스트")
class AppStoreClientTest {

    private static final String APP_RECEIPT = "test.app.receipt";
    private static final String TRANSACTION_ID = "test.transaction.id";
    private static final String BEARER_TOKEN = "Bearer test.jwt.token";
    private static final String SIGNED_TRANSACTION_INFO = "signed.transaction.info";

    @Mock
    private AppStoreFeignClient feignClient;

    @Mock
    private AppStoreTokenService appStoreTokenService;

    @Mock
    private ReceiptUtility receiptUtil;

    @Mock
    private SignedDataVerifier signedDataVerifier;

    @Mock
    private AppStoreTransactionResponse transactionResponse;

    @Mock
    private JWSTransactionDecodedPayload decodedPayload;

    @InjectMocks
    private AppStoreClient appStoreClient;

    @Nested
    @DisplayName("getTransactionDecodedPayload 메서드는")
    class GetTransactionDecodedPayloadTests {

        @DisplayName("정상적으로 트랜잭션 정보를 조회하고 디코딩된 페이로드를 반환한다")
        @Test
        void whenSuccessful_returnsDecodedPayload() throws IOException, VerificationException {
            // given
            when(receiptUtil.extractTransactionIdFromAppReceipt(APP_RECEIPT)).thenReturn(TRANSACTION_ID);
            when(appStoreTokenService.generateToken()).thenReturn(BEARER_TOKEN);
            when(feignClient.getTransactionInfo(TRANSACTION_ID, BEARER_TOKEN)).thenReturn(transactionResponse);
            when(transactionResponse.getSignedTransactionInfo()).thenReturn(SIGNED_TRANSACTION_INFO);
            when(signedDataVerifier.verifyAndDecodeTransaction(SIGNED_TRANSACTION_INFO)).thenReturn(decodedPayload);

            // when
            JWSTransactionDecodedPayload result = appStoreClient.getTransactionDecodedPayload(APP_RECEIPT);

            // then
            assertThat(result).isEqualTo(decodedPayload);
            verify(receiptUtil).extractTransactionIdFromAppReceipt(APP_RECEIPT);
            verify(appStoreTokenService).generateToken();
            verify(feignClient).getTransactionInfo(TRANSACTION_ID, BEARER_TOKEN);
            verify(signedDataVerifier).verifyAndDecodeTransaction(SIGNED_TRANSACTION_INFO);
        }

        @DisplayName("앱 영수증에서 트랜잭션 ID 추출 시 null이 반환되면 InvalidAppReceiptException을 던진다")
        @Test
        void whenTransactionIdIsNull_throwsInvalidAppReceiptException() throws IOException {
            // given
            when(receiptUtil.extractTransactionIdFromAppReceipt(APP_RECEIPT)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> appStoreClient.getTransactionDecodedPayload(APP_RECEIPT))
                .isInstanceOf(InvalidAppReceiptException.class)
                .hasMessage("앱 영수증에 TransactionId가 없습니다.");

            verify(appStoreTokenService, never()).generateToken();
            verify(feignClient, never()).getTransactionInfo(anyString(), anyString());
        }

        @DisplayName("앱 영수증에서 트랜잭션 ID 추출 시 IllegalArgumentException이 발생하면 InvalidAppReceiptException을 던진다")
        @Test
        void whenIllegalArgumentExceptionOccurs_throwsInvalidAppReceiptException() throws IOException {
            // given
            IllegalArgumentException cause = new IllegalArgumentException("Invalid receipt format");
            when(receiptUtil.extractTransactionIdFromAppReceipt(APP_RECEIPT)).thenThrow(cause);

            // when & then
            assertThatThrownBy(() -> appStoreClient.getTransactionDecodedPayload(APP_RECEIPT))
                .isInstanceOf(InvalidAppReceiptException.class)
                .hasCause(cause);
        }

        @DisplayName("앱 영수증에서 트랜잭션 ID 추출 시 IOException이 발생하면 AppStoreClientException을 던진다")
        @Test
        void whenIOExceptionOccurs_throwsAppStoreClientException() throws IOException {
            // given
            IOException cause = new IOException("IO error");
            when(receiptUtil.extractTransactionIdFromAppReceipt(APP_RECEIPT)).thenThrow(cause);

            // when & then
            assertThatThrownBy(() -> appStoreClient.getTransactionDecodedPayload(APP_RECEIPT))
                .isInstanceOf(AppStoreClientException.class)
                .hasCause(cause);
        }

        @DisplayName("Feign 클라이언트에서 401 에러가 발생하면 토큰을 강제 갱신한다")
        @Test
        void whenUnauthorizedError_forcesTokenRefresh() throws IOException {
            // given
            FeignException.Unauthorized unauthorizedException = mock(FeignException.Unauthorized.class);
            when(receiptUtil.extractTransactionIdFromAppReceipt(APP_RECEIPT)).thenReturn(TRANSACTION_ID);
            when(appStoreTokenService.generateToken()).thenReturn(BEARER_TOKEN);
            when(feignClient.getTransactionInfo(TRANSACTION_ID, BEARER_TOKEN)).thenThrow(unauthorizedException);

            // when & then
            assertThatThrownBy(() -> appStoreClient.getTransactionDecodedPayload(APP_RECEIPT))
                .isInstanceOf(FeignException.class);

            verify(appStoreTokenService).forceRefreshToken();
        }

        @DisplayName("Feign 클라이언트에서 401이 아닌 에러가 발생하면 토큰 갱신하지 않는다")
        @Test
        void whenNonUnauthorizedError_doesNotRefreshToken() throws IOException {
            // given
            FeignException.InternalServerError serverException = mock(FeignException.InternalServerError.class);
            when(receiptUtil.extractTransactionIdFromAppReceipt(APP_RECEIPT)).thenReturn(TRANSACTION_ID);
            when(appStoreTokenService.generateToken()).thenReturn(BEARER_TOKEN);
            when(feignClient.getTransactionInfo(TRANSACTION_ID, BEARER_TOKEN)).thenThrow(serverException);

            // when & then
            assertThatThrownBy(() -> appStoreClient.getTransactionDecodedPayload(APP_RECEIPT))
                .isInstanceOf(FeignException.class);

            verify(appStoreTokenService, never()).forceRefreshToken();
        }

        @DisplayName("트랜잭션 정보 검증 중 VerificationException이 발생하면 AppStoreClientException을 던진다")
        @Test
        void whenVerificationExceptionOccurs_throwsAppStoreClientException() throws IOException, VerificationException {
            // given
            VerificationException cause = mock(VerificationException.class);
            when(receiptUtil.extractTransactionIdFromAppReceipt(APP_RECEIPT)).thenReturn(TRANSACTION_ID);
            when(appStoreTokenService.generateToken()).thenReturn(BEARER_TOKEN);
            when(feignClient.getTransactionInfo(TRANSACTION_ID, BEARER_TOKEN)).thenReturn(transactionResponse);
            when(transactionResponse.getSignedTransactionInfo()).thenReturn(SIGNED_TRANSACTION_INFO);
            when(signedDataVerifier.verifyAndDecodeTransaction(SIGNED_TRANSACTION_INFO)).thenThrow(cause);

            // when & then
            assertThatThrownBy(() -> appStoreClient.getTransactionDecodedPayload(APP_RECEIPT))
                .isInstanceOf(AppStoreClientException.class)
                .hasCause(cause);
        }
    }

    @Nested
    @DisplayName("getTransactionDecodedPayloadFallback 메서드는")
    class GetTransactionDecodedPayloadFallbackTests {

        @DisplayName("400 에러가 발생하면 InvalidTransactionIdException을 던진다")
        @Test
        void when400Error_throwsInvalidTransactionIdException() {
            // given
            FeignException badRequestException = new FeignException.BadRequest(
                "Bad Request", mock(feign.Request.class), null, null
            );

            // when & then
            assertThatThrownBy(
                () -> appStoreClient.getTransactionDecodedPayloadFallback(APP_RECEIPT, badRequestException))
                .isInstanceOf(InvalidTransactionIdException.class)
                .hasCause(badRequestException);
        }

        @DisplayName("404 에러가 발생하면 InvalidTransactionIdException을 던진다")
        @Test
        void when404Error_throwsInvalidTransactionIdException() {
            // given
            FeignException notFoundException = new FeignException.NotFound(
                "Not Found", mock(feign.Request.class), null, null
            );

            // when & then
            assertThatThrownBy(
                () -> appStoreClient.getTransactionDecodedPayloadFallback(APP_RECEIPT, notFoundException))
                .isInstanceOf(InvalidTransactionIdException.class)
                .hasCause(notFoundException);
        }

        @DisplayName("500 에러가 발생하면 AppStoreClientException을 던진다")
        @Test
        void when500Error_throwsAppStoreClientException() {
            // given
            FeignException serverException = new FeignException.InternalServerError(
                "Internal Server Error", mock(feign.Request.class), null, null
            );

            // when & then
            assertThatThrownBy(() -> appStoreClient.getTransactionDecodedPayloadFallback(APP_RECEIPT, serverException))
                .isInstanceOf(AppStoreClientException.class)
                .hasCause(serverException);
        }

        @DisplayName("FeignException이 아닌 예외가 발생하면 AppStoreClientException을 던진다")
        @Test
        void whenNonFeignException_throwsAppStoreClientException() {
            // given
            RuntimeException generalException = new RuntimeException("General error");

            // when & then
            assertThatThrownBy(() -> appStoreClient.getTransactionDecodedPayloadFallback(APP_RECEIPT, generalException))
                .isInstanceOf(AppStoreClientException.class)
                .hasCause(generalException);
        }
    }
}
