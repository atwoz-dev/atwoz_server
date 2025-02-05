package atwoz.atwoz.payment.application;

import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.payment.application.exception.InvalidOrderException;
import atwoz.atwoz.payment.application.exception.OrderAlreadyExistsException;
import atwoz.atwoz.payment.domain.*;
import atwoz.atwoz.payment.infra.AppStoreClientImpl;
import atwoz.atwoz.payment.domain.OrderCommandRepository;
import com.apple.itunes.storekit.model.TransactionInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppStorePaymentService {
    private final AppStoreClientImpl appStoreClient;
    private final TokenParser tokenParser;
    private final OrderCommandRepository orderCommandRepository;

    public void verifyReceipt(String receiptToken, Long memberId) {
        TransactionInfo transactionInfo = getTransactionInfo(receiptToken);
        verifyTransactionInfo(transactionInfo);
        createOrder(memberId, transactionInfo);
        Events.raise(HeartPurchased.of(memberId, transactionInfo.getProductId(), transactionInfo.getQuantity()));
    }

    private TransactionInfo getTransactionInfo(String receiptToken) {
        TransactionInfoResponse transactionInfoResponse = appStoreClient.getTransactionInfo(receiptToken);
        String signedTransactionInfo = transactionInfoResponse.getSignedTransactionInfo();
        return tokenParser.parseToTransactionInfo(signedTransactionInfo);
    }

    private void verifyTransactionInfo(TransactionInfo transactionInfo) {
        if (transactionInfo.isRevoked()) {
            throw new InvalidOrderException();
        }
        if (orderCommandRepository.existsByTransactionIdAndPaymentMethod(transactionInfo.getTransactionId(), PaymentMethod.APP_STORE)) {
            throw new OrderAlreadyExistsException();
        }
    }

    private void createOrder(Long memberId, TransactionInfo transactionInfo) {
        Order order = Order.of(memberId, transactionInfo.getTransactionId(), PaymentMethod.APP_STORE);
        orderCommandRepository.save(order);
    }
}
