package atwoz.atwoz.payment.application;

import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.payment.application.exception.InvalidOrderException;
import atwoz.atwoz.payment.application.exception.OrderAlreadyExistsException;
import atwoz.atwoz.payment.domain.Order;
import atwoz.atwoz.payment.domain.PaymentMethod;
import atwoz.atwoz.payment.domain.TokenParser;
import atwoz.atwoz.payment.domain.TransactionInfo;
import atwoz.atwoz.payment.infra.AppStoreClientImpl;
import atwoz.atwoz.payment.domain.OrderCommandRepository;
import com.apple.itunes.storekit.model.TransactionInfoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppStorePaymentServiceTest {

    @Mock
    private AppStoreClientImpl appStoreClient;

    @Mock
    private TokenParser tokenParser;

    @Mock
    private OrderCommandRepository orderCommandRepository;

    @InjectMocks
    private AppStorePaymentService appStorePaymentService;

    @Test
    @DisplayName("receiptToken이 검증되고, transactionInfo가 paid 상태이며, transactionId가 처리된 기록이 없는 경우 주문을 생성하고 이벤트를 발생시킨다.")
    public void successWhenReceiptTokenIsVerifiedAndTransactionInfoIsPaidAndTransactionIdIsNotExists() {
        // Given
        String receiptToken = "receiptToken";
        Long memberId = 1L;
        String signedTransactionInfo = "signedTransactionInfo";
        String transactionId = "transactionId";
        String productId = "productId";
        Integer quantity = 1;

        TransactionInfoResponse transactionInfoResponse = new TransactionInfoResponse();
        transactionInfoResponse.signedTransactionInfo(signedTransactionInfo);
        when(appStoreClient.getTransactionInfo(receiptToken)).thenReturn(transactionInfoResponse);

        TransactionInfo transactionInfo = mock(TransactionInfo.class);
        when(tokenParser.parseToTransactionInfo(signedTransactionInfo)).thenReturn(transactionInfo);
        when(transactionInfo.isRevoked()).thenReturn(false);
        when(transactionInfo.getTransactionId()).thenReturn(transactionId);
        when(transactionInfo.getProductId()).thenReturn(productId);
        when(transactionInfo.getQuantity()).thenReturn(quantity);

        when(orderCommandRepository.existsByTransactionIdAndPaymentMethod(transactionId, PaymentMethod.APP_STORE))
                .thenReturn(false);

        try (MockedStatic<Events> eventsMockedStatic = mockStatic(Events.class)) {
            // When
            appStorePaymentService.verifyReceipt(receiptToken, memberId);

            // Then
            verify(orderCommandRepository, times(1)).save(any(Order.class));
            eventsMockedStatic.verify(() ->
                    Events.raise(argThat((HeartPurchased event) ->
                            event.getMemberId().equals(memberId) &&
                                    event.getProductId().equals(productId) &&
                                    event.getQuantity().equals(quantity)
                    )), times(1));
        }
    }

    @Test
    @DisplayName("receiptToken이 검증되고, transactionInfo가 revoked 상태인 경우 InvalidOrderException을 발생시킨다.")
    public void throwInvalidOrderExceptionWhenTransactionInfoIsRevoked() {
        // Given
        String receiptToken = "receiptToken";
        Long memberId = 1L;
        String signedTransactionInfo = "signedTransactionInfo";
        String transactionId = "transactionId";
        String productId = "productId";
        Integer quantity = 1;

        TransactionInfoResponse transactionInfoResponse = new TransactionInfoResponse();
        transactionInfoResponse.signedTransactionInfo(signedTransactionInfo);
        when(appStoreClient.getTransactionInfo(receiptToken)).thenReturn(transactionInfoResponse);

        TransactionInfo transactionInfo = mock(TransactionInfo.class);
        when(tokenParser.parseToTransactionInfo(signedTransactionInfo)).thenReturn(transactionInfo);
        when(transactionInfo.isRevoked()).thenReturn(true);

        try (MockedStatic<Events> eventsMockedStatic = mockStatic(Events.class)) {
            // When & Then
            assertThatThrownBy(() ->
                    appStorePaymentService.verifyReceipt(receiptToken, memberId))
                    .isInstanceOf(InvalidOrderException.class);

            verify(orderCommandRepository, never()).save(any(Order.class));
            eventsMockedStatic.verify(() -> Events.raise(any(HeartPurchased.class)), never());
        }
    }

    @Test
    @DisplayName("receiptToken이 검증되고, transactionInfo가 paid 상태이며, transactionId가 처리된 기록이 있는 경우 OrderAlreadyExistsException을 발생시킨다.")
    public void throwOrderAlreadyExistsExceptionWhenTransactionIdIsAlreadyExists() {
        // Given
        String receiptToken = "receiptToken";
        Long memberId = 1L;
        String signedTransactionInfo = "signedTransactionInfo";
        String transactionId = "transactionId";
        String productId = "productId";
        Integer quantity = 1;

        TransactionInfoResponse transactionInfoResponse = new TransactionInfoResponse();
        transactionInfoResponse.signedTransactionInfo(signedTransactionInfo);
        when(appStoreClient.getTransactionInfo(receiptToken)).thenReturn(transactionInfoResponse);

        TransactionInfo transactionInfo = mock(TransactionInfo.class);
        when(tokenParser.parseToTransactionInfo(signedTransactionInfo)).thenReturn(transactionInfo);
        when(transactionInfo.isRevoked()).thenReturn(false);
        when(transactionInfo.getTransactionId()).thenReturn(transactionId);

        when(orderCommandRepository.existsByTransactionIdAndPaymentMethod(transactionId, PaymentMethod.APP_STORE))
                .thenReturn(true);

        try (MockedStatic<Events> eventsMockedStatic = mockStatic(Events.class)) {
            // When & Then
            assertThatThrownBy(() ->
                    appStorePaymentService.verifyReceipt(receiptToken, memberId))
                    .isInstanceOf(OrderAlreadyExistsException.class);

            verify(orderCommandRepository, never()).save(any(Order.class));
            eventsMockedStatic.verify(() -> Events.raise(any(HeartPurchased.class)), never());
        }
    }
}
