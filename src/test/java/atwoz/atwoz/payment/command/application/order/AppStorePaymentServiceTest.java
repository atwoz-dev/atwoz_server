package atwoz.atwoz.payment.command.application.order;

import atwoz.atwoz.payment.command.application.order.exception.HeartPurchaseOptionNotFoundException;
import atwoz.atwoz.payment.command.application.order.exception.InvalidOrderException;
import atwoz.atwoz.payment.command.application.order.exception.OrderAlreadyExistsException;
import atwoz.atwoz.payment.command.domain.heartpurchaseoption.HeartPurchaseOption;
import atwoz.atwoz.payment.command.domain.heartpurchaseoption.HeartPurchaseOptionCommandRepository;
import atwoz.atwoz.payment.command.domain.order.Order;
import atwoz.atwoz.payment.command.domain.order.OrderCommandRepository;
import atwoz.atwoz.payment.command.domain.order.PaymentMethod;
import atwoz.atwoz.payment.command.infra.order.AppStoreClient;
import com.apple.itunes.storekit.model.JWSTransactionDecodedPayload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppStorePaymentServiceTest {

    @Mock
    private AppStoreClient appStoreClient;

    @Mock
    private OrderCommandRepository orderCommandRepository;

    @Mock
    private HeartPurchaseOptionCommandRepository heartPurchaseOptionCommandRepository;

    @InjectMocks
    private AppStorePaymentService appStorePaymentService;

    @Test
    @DisplayName("receiptToken이 검증되고, transactionInfo가 paid 상태이며, transactionId가 처리된 기록이 없는 경우 주문을 생성하고 하트 구매 옵션을 구매한다.")
    void successWhenReceiptTokenIsVerifiedAndTransactionInfoIsPaidAndTransactionIdIsNotExists() {
        // Given
        String receiptToken = "receiptToken";
        Long memberId = 1L;
        String transactionId = "transactionId";
        String productId = "productId";
        Integer quantity = 1;

        JWSTransactionDecodedPayload decodedPayload = mock(JWSTransactionDecodedPayload.class);
        when(decodedPayload.getRevocationDate()).thenReturn(null);
        when(decodedPayload.getTransactionId()).thenReturn(transactionId);
        when(decodedPayload.getProductId()).thenReturn(productId);
        when(decodedPayload.getQuantity()).thenReturn(quantity);

        when(appStoreClient.getTransactionDecodedPayload(receiptToken)).thenReturn(decodedPayload);

        when(orderCommandRepository.existsByTransactionIdAndPaymentMethod(transactionId, PaymentMethod.APP_STORE))
            .thenReturn(false);

        HeartPurchaseOption heartPurchaseOption = mock(HeartPurchaseOption.class);
        when(heartPurchaseOptionCommandRepository.findByProductId(productId)).thenReturn(
            Optional.of(heartPurchaseOption));

        // When
        appStorePaymentService.verifyReceipt(receiptToken, memberId);

        // Then
        verify(orderCommandRepository).save(any(Order.class));
        verify(heartPurchaseOption).purchase(memberId, quantity);
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

        verify(orderCommandRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("receiptToken이 검증되고, transactionInfo가 paid 상태이며, transactionId가 처리된 기록이 있는 경우 OrderAlreadyExistsException을 발생시킨다.")
    void throwOrderAlreadyExistsExceptionWhenTransactionIdIsAlreadyExists() {
        // Given
        String receiptToken = "receiptToken";
        Long memberId = 1L;
        String transactionId = "transactionId";

        JWSTransactionDecodedPayload decodedPayload = mock(JWSTransactionDecodedPayload.class);
        when(decodedPayload.getRevocationDate()).thenReturn(null);
        when(decodedPayload.getTransactionId()).thenReturn(transactionId);

        when(appStoreClient.getTransactionDecodedPayload(receiptToken)).thenReturn(decodedPayload);

        when(orderCommandRepository.existsByTransactionIdAndPaymentMethod(transactionId, PaymentMethod.APP_STORE))
            .thenReturn(true);

        // When & Then
        assertThatThrownBy(() ->
            appStorePaymentService.verifyReceipt(receiptToken, memberId))
            .isInstanceOf(OrderAlreadyExistsException.class);
        verify(orderCommandRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("heartPurchaseOption이 없으면 예외를 던진다.")
    void throwExceptionWhenHeartPurchaseOptionIsNotExists() {
        // Given
        String receiptToken = "receiptToken";
        Long memberId = 1L;
        String transactionId = "transactionId";
        String productId = "productId";

        JWSTransactionDecodedPayload decodedPayload = mock(JWSTransactionDecodedPayload.class);
        when(decodedPayload.getRevocationDate()).thenReturn(null);
        when(decodedPayload.getTransactionId()).thenReturn(transactionId);
        when(decodedPayload.getProductId()).thenReturn(productId);

        when(appStoreClient.getTransactionDecodedPayload(receiptToken)).thenReturn(decodedPayload);

        when(orderCommandRepository.existsByTransactionIdAndPaymentMethod(transactionId, PaymentMethod.APP_STORE))
            .thenReturn(false);

        when(heartPurchaseOptionCommandRepository.findByProductId(productId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
            appStorePaymentService.verifyReceipt(receiptToken, memberId))
            .isInstanceOf(HeartPurchaseOptionNotFoundException.class);
        verify(orderCommandRepository).save(any(Order.class));
    }
}
