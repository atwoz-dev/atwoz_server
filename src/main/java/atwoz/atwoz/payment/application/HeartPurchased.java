package atwoz.atwoz.payment.application;

import atwoz.atwoz.common.event.Event;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class HeartPurchased extends Event {
    private final Long memberId;
    private final String productId;
    private final Integer quantity;

    public static HeartPurchased of(Long memberId, String productId, Integer quantity) {
        return new HeartPurchased(memberId, productId, quantity);
    }

    private HeartPurchased(@NonNull Long memberId, @NonNull String productId, @NonNull Integer quantity) {
        this.memberId = memberId;
        this.productId = productId;
        this.quantity = quantity;
    }
}
