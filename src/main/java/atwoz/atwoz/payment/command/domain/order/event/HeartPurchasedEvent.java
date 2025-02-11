package atwoz.atwoz.payment.command.domain.order.event;

import atwoz.atwoz.common.event.Event;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class HeartPurchasedEvent extends Event {
    private final Long memberId;
    private final Long amount;

    public static HeartPurchasedEvent of(Long memberId, Long amount) {
        return new HeartPurchasedEvent(memberId, amount);
    }

    private HeartPurchasedEvent(@NonNull Long memberId, @NonNull Long amount) {
        this.memberId = memberId;
        this.amount = amount;
    }
}
