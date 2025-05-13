package atwoz.atwoz.payment.command.infra.order;

import atwoz.atwoz.payment.command.application.order.OrderService;
import atwoz.atwoz.payment.command.domain.order.PaymentMethod;
import atwoz.atwoz.payment.command.domain.order.event.AppStoreReceiptVerifiedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReceiptVerifiedEventHandler {
    private final OrderService orderService;

    @EventListener(value = AppStoreReceiptVerifiedEvent.class)
    public void handle(AppStoreReceiptVerifiedEvent event) {
        orderService.processReceipt(event.getMemberId(), event.getTransactionId(), event.getProductId(),
            event.getQuantity(), PaymentMethod.APP_STORE);
    }
}
