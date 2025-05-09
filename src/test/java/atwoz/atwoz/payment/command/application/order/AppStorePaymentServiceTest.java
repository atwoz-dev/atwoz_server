package atwoz.atwoz.payment.command.application.order;

import atwoz.atwoz.payment.command.application.order.exception.HeartPurchaseOptionNotFoundException;
import atwoz.atwoz.payment.command.application.order.exception.InvalidOrderException;
import atwoz.atwoz.payment.command.application.order.exception.OrderAlreadyExistsException;
import atwoz.atwoz.payment.command.domain.heartpurchaseoption.HeartPurchaseOption;
import atwoz.atwoz.payment.command.domain.heartpurchaseoption.HeartPurchaseOptionCommandRepository;
import atwoz.atwoz.payment.command.domain.order.Order;
import atwoz.atwoz.payment.command.domain.order.OrderCommandRepository;
import atwoz.atwoz.payment.command.domain.order.PaymentMethod;
import atwoz.atwoz.payment.command.domain.order.TokenParser;
import atwoz.atwoz.payment.command.infra.order.AppStoreClient;
import atwoz.atwoz.payment.command.infra.order.TransactionInfo;
import com.apple.itunes.storekit.model.TransactionInfoResponse;
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
public class AppStorePaymentServiceTest {

    @Mock
    private AppStoreClient appStoreClient;

    @Mock
    private TokenParser tokenParser;

    @Mock
    private OrderCommandRepository orderCommandRepository;

    @Mock
    private HeartPurchaseOptionCommandRepository heartPurchaseOptionCommandRepository;

    @InjectMocks
    private AppStorePaymentService appStorePaymentService;

    @Test
    @DisplayName("receiptToken이 검증되고, transactionInfo가 paid 상태이며, transactionId가 처리된 기록이 없는 경우 주문을 생성하고 하트 구매 옵션을 구매한다.")
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
    public void throwInvalidOrderExceptionWhenTransactionInfoIsRevoked() {
        // Given
        String receiptToken = "receiptToken";
        Long memberId = 1L;
        String signedTransactionInfo = "signedTransactionInfo";

        TransactionInfoResponse transactionInfoResponse = new TransactionInfoResponse();
        transactionInfoResponse.signedTransactionInfo(signedTransactionInfo);
        when(appStoreClient.getTransactionInfo(receiptToken)).thenReturn(transactionInfoResponse);

        TransactionInfo transactionInfo = mock(TransactionInfo.class);
        when(tokenParser.parseToTransactionInfo(signedTransactionInfo)).thenReturn(transactionInfo);
        when(transactionInfo.isRevoked()).thenReturn(true);

        // When & Then
        assertThatThrownBy(() ->
            appStorePaymentService.verifyReceipt(receiptToken, memberId))
            .isInstanceOf(InvalidOrderException.class);

        verify(orderCommandRepository, never()).save(any(Order.class));
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

        // When & Then
        assertThatThrownBy(() ->
            appStorePaymentService.verifyReceipt(receiptToken, memberId))
            .isInstanceOf(OrderAlreadyExistsException.class);
        verify(orderCommandRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("heartPurchaseOption이 없으면 예외를 던진다.")
    public void throwExceptionWhenHeartPurchaseOptionIsNotExists() {
        // Given
        String receiptToken = "receiptToken";
        Long memberId = 1L;
        String signedTransactionInfo = "signedTransactionInfo";
        String transactionId = "transactionId";
        String productId = "productId";

        TransactionInfoResponse transactionInfoResponse = new TransactionInfoResponse();
        transactionInfoResponse.signedTransactionInfo(signedTransactionInfo);
        when(appStoreClient.getTransactionInfo(receiptToken)).thenReturn(transactionInfoResponse);

        TransactionInfo transactionInfo = mock(TransactionInfo.class);
        when(tokenParser.parseToTransactionInfo(signedTransactionInfo)).thenReturn(transactionInfo);
        when(transactionInfo.isRevoked()).thenReturn(false);
        when(transactionInfo.getTransactionId()).thenReturn(transactionId);
        when(transactionInfo.getProductId()).thenReturn(productId);

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
