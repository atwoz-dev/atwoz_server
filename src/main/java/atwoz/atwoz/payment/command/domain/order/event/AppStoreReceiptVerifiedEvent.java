package atwoz.atwoz.payment.command.domain.order.event;

import atwoz.atwoz.common.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AppStoreReceiptVerifiedEvent extends Event {
    private final long memberId;
    private final String transactionId;
    private final String productId;
    private final int quantity;

    public static AppStoreReceiptVerifiedEvent of(long memberId, String transactionId, String productId,
        int quantity) {
        return new AppStoreReceiptVerifiedEvent(memberId, transactionId, productId, quantity);
    }
}
