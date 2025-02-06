package atwoz.atwoz.payment.application;

import atwoz.atwoz.payment.application.exception.HeartPurchaseOptionNotFoundException;
import atwoz.atwoz.heartpurchaseoption.domain.HeartPurchaseOption;
import atwoz.atwoz.heartpurchaseoption.domain.HeartPurchaseOptionRepository;
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
    private final HeartPurchaseOptionRepository heartPurchaseOptionRepository;

    public void verifyReceipt(String receiptToken, Long memberId) {
        TransactionInfo transactionInfo = getTransactionInfo(receiptToken);
        verifyTransactionInfo(transactionInfo);
        createOrder(memberId, transactionInfo);
        purchaseHeart(memberId, transactionInfo.getProductId(), transactionInfo.getQuantity());
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

    private void purchaseHeart(Long memberId, String productId, Integer quantity) {
        HeartPurchaseOption heartPurchaseOption = heartPurchaseOptionRepository.findByProductId(productId)
                .orElseThrow(() -> new HeartPurchaseOptionNotFoundException("하트 구매 옵션이 존재하지 않습니다. product id:" + productId));
        heartPurchaseOption.purchase(memberId, quantity);
    }
}
