package atwoz.atwoz.payment.command.application.order;

import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.payment.command.application.order.exception.InvalidOrderException;
import atwoz.atwoz.payment.command.domain.order.event.AppStoreReceiptVerifiedEvent;
import atwoz.atwoz.payment.command.infra.order.AppStoreClient;
import com.apple.itunes.storekit.model.JWSTransactionDecodedPayload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppStorePaymentServiceTest {

    @Mock
    private AppStoreClient appStoreClient;

    @InjectMocks
    private AppStorePaymentService appStorePaymentService;

    @Test
    @DisplayName("receiptToken이 검증되고, transactionInfo가 paid 상태이며, transactionId가 처리된 기록이 없는 경우 주문을 생성하고 하트 구매 옵션을 구매한다.")
    void successWhenReceiptTokenIsVerifiedAndTransactionInfoIsPaidAndTransactionIdIsNotExists() {
        // Given
        String receiptToken = "receiptToken";
        long memberId = 1L;
        String transactionId = "transactionId";
        String productId = "productId";
        int quantity = 1;

        JWSTransactionDecodedPayload decodedPayload = mock(JWSTransactionDecodedPayload.class);
        when(decodedPayload.getRevocationDate()).thenReturn(null);
        when(decodedPayload.getTransactionId()).thenReturn(transactionId);
        when(decodedPayload.getProductId()).thenReturn(productId);
        when(decodedPayload.getQuantity()).thenReturn(quantity);

        when(appStoreClient.getTransactionDecodedPayload(receiptToken)).thenReturn(decodedPayload);

        // When & Then
        try (MockedStatic<Events> eventsMockedStatic = mockStatic(Events.class)) {
            appStorePaymentService.verifyReceipt(receiptToken, memberId);
            eventsMockedStatic.verify(
                () -> Events.raise(argThat(
                        event -> event instanceof AppStoreReceiptVerifiedEvent
                            && ((AppStoreReceiptVerifiedEvent) event).getMemberId() == memberId
                            && ((AppStoreReceiptVerifiedEvent) event).getTransactionId().equals(transactionId)
                            && ((AppStoreReceiptVerifiedEvent) event).getProductId().equals(productId)
                            && ((AppStoreReceiptVerifiedEvent) event).getQuantity() == quantity
                    )
                )
            );
        }
    }

    @Test
    @DisplayName("receiptToken이 검증되고, transactionInfo가 revoked 상태인 경우 InvalidOrderException을 발생시킨다.")
    void throwInvalidOrderExceptionWhenTransactionInfoIsRevoked() {
        // Given
        String receiptToken = "receiptToken";
        Long memberId = 1L;

        JWSTransactionDecodedPayload decodedPayload = mock(JWSTransactionDecodedPayload.class);
        Long revocationDate = 123456789L;
        when(decodedPayload.getRevocationDate()).thenReturn(revocationDate);

        when(appStoreClient.getTransactionDecodedPayload(receiptToken)).thenReturn(decodedPayload);

        // When & Then
        assertThatThrownBy(() ->
            appStorePaymentService.verifyReceipt(receiptToken, memberId))
            .isInstanceOf(InvalidOrderException.class);
    }
}
