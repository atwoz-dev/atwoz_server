package atwoz.atwoz.member.command.domain.member.event;

import atwoz.atwoz.common.event.Event;
import lombok.Getter;

@Getter
public class PurchaseHeartGainedEvent extends Event {
    private final Long memberId;
    private final Long amount;
    private final Long missionHeartBalance;
    private final Long purchaseHeartBalance;

    public static PurchaseHeartGainedEvent of(Long memberId, Long amount, Long missionHeartBalance, Long purchaseHeartBalance) {
        return new PurchaseHeartGainedEvent(memberId, amount, missionHeartBalance, purchaseHeartBalance);
    }

    private PurchaseHeartGainedEvent(Long memberId, Long amount, Long missionHeartBalance, Long purchaseHeartBalance) {
        this.memberId = memberId;
        this.amount = amount;
        this.missionHeartBalance = missionHeartBalance;
        this.purchaseHeartBalance = purchaseHeartBalance;
    }
}
