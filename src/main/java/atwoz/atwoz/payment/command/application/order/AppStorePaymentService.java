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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppStorePaymentService {
    private final AppStoreClient appStoreClient;
    private final TokenParser tokenParser;
    private final OrderCommandRepository orderCommandRepository;
    private final HeartPurchaseOptionCommandRepository heartPurchaseOptionCommandRepository;

    @Transactional
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
        if (orderCommandRepository.existsByTransactionIdAndPaymentMethod(transactionInfo.getTransactionId(),
            PaymentMethod.APP_STORE)) {
            throw new OrderAlreadyExistsException();
        }
    }

    private void createOrder(Long memberId, TransactionInfo transactionInfo) {
        Order order = Order.of(memberId, transactionInfo.getTransactionId(), PaymentMethod.APP_STORE);
        orderCommandRepository.save(order);
    }

    private void purchaseHeart(Long memberId, String productId, Integer quantity) {
        HeartPurchaseOption heartPurchaseOption = heartPurchaseOptionCommandRepository.findByProductId(productId)
            .orElseThrow(
                () -> new HeartPurchaseOptionNotFoundException("하트 구매 옵션이 존재하지 않습니다. product id:" + productId));
        heartPurchaseOption.purchase(memberId, quantity);
    }
}
