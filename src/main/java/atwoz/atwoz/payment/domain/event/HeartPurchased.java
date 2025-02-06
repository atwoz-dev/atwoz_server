package atwoz.atwoz.payment.domain.event;

import atwoz.atwoz.common.event.Event;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class HeartPurchased extends Event {
    private final Long memberId;
    private final Long amount;

    public static HeartPurchased of(Long memberId, Long amount) {
        return new HeartPurchased(memberId, amount);
    }

    private HeartPurchased(@NonNull Long memberId, @NonNull Long amount) {
        this.memberId = memberId;
        this.amount = amount;
    }
}
