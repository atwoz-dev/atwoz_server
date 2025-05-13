package atwoz.atwoz.payment.command.application.order;

import atwoz.atwoz.payment.command.application.order.exception.HeartPurchaseOptionNotFoundException;
import atwoz.atwoz.payment.command.application.order.exception.OrderAlreadyExistsException;
import atwoz.atwoz.payment.command.domain.heartpurchaseoption.HeartPurchaseOption;
import atwoz.atwoz.payment.command.domain.heartpurchaseoption.HeartPurchaseOptionCommandRepository;
import atwoz.atwoz.payment.command.domain.order.Order;
import atwoz.atwoz.payment.command.domain.order.OrderCommandRepository;
import atwoz.atwoz.payment.command.domain.order.PaymentMethod;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderCommandRepository orderCommandRepository;
    private final HeartPurchaseOptionCommandRepository heartPurchaseOptionCommandRepository;

    @Transactional
    public void processReceipt(long memberId, String transactionId, String productId, int quantity,
        PaymentMethod paymentMethod) {
        verifyTransactionId(transactionId, paymentMethod);
        createOrder(memberId, transactionId, paymentMethod);
        purchaseHeart(memberId, productId, quantity);
    }

    private void verifyTransactionId(String transactionId, PaymentMethod paymentMethod) {
        if (orderCommandRepository.existsByTransactionIdAndPaymentMethod(transactionId, paymentMethod)) {
            throw new OrderAlreadyExistsException();
        }
    }

    private void createOrder(Long memberId, String transactionId, PaymentMethod paymentMethod) {
        Order order = Order.of(memberId, transactionId, paymentMethod);
        orderCommandRepository.save(order);
    }

    private void purchaseHeart(Long memberId, String productId, Integer quantity) {
        HeartPurchaseOption heartPurchaseOption = heartPurchaseOptionCommandRepository.findByProductId(productId)
            .orElseThrow(
                () -> new HeartPurchaseOptionNotFoundException("하트 구매 옵션이 존재하지 않습니다. product id:" + productId));
        heartPurchaseOption.purchase(memberId, quantity);
    }
}
