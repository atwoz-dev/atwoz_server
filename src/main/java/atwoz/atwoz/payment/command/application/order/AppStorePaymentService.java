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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppStorePaymentService {
    private final AppStoreClient appStoreClient;
    private final OrderCommandRepository orderCommandRepository;
    private final HeartPurchaseOptionCommandRepository heartPurchaseOptionCommandRepository;

    @Transactional
    public void verifyReceipt(String appReceipt, Long memberId) {
        JWSTransactionDecodedPayload decodedPayload = getTransactionDecodedPayload(appReceipt);
        verifyTransactionInfo(decodedPayload);
        createOrder(memberId, decodedPayload);
        purchaseHeart(memberId, decodedPayload.getProductId(), decodedPayload.getQuantity());
    }

    private JWSTransactionDecodedPayload getTransactionDecodedPayload(String appReceipt) {
        return appStoreClient.getTransactionDecodedPayload(appReceipt);
    }

    private void verifyTransactionInfo(JWSTransactionDecodedPayload decodedPayload) {
        if (decodedPayload.getRevocationDate() != null) {
            throw new InvalidOrderException();
        }
        if (orderCommandRepository.existsByTransactionIdAndPaymentMethod(decodedPayload.getTransactionId(),
            PaymentMethod.APP_STORE)) {
            throw new OrderAlreadyExistsException();
        }
    }

    private void createOrder(Long memberId, JWSTransactionDecodedPayload decodedPayload) {
        Order order = Order.of(memberId, decodedPayload.getTransactionId(), PaymentMethod.APP_STORE);
        orderCommandRepository.save(order);
    }

    private void purchaseHeart(Long memberId, String productId, Integer quantity) {
        HeartPurchaseOption heartPurchaseOption = heartPurchaseOptionCommandRepository.findByProductId(productId)
            .orElseThrow(
                () -> new HeartPurchaseOptionNotFoundException("하트 구매 옵션이 존재하지 않습니다. product id:" + productId));
        heartPurchaseOption.purchase(memberId, quantity);
    }
}
