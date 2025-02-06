package atwoz.atwoz.member.command.domain.member.event;

import atwoz.atwoz.common.event.Event;
import lombok.Getter;

@Getter
public class PurchaseHeartGained extends Event {
    private final Long memberId;
    private final Long amount;
    private final Long missionHeartBalance;
    private final Long purchaseHeartBalance;

    public static PurchaseHeartGained of(Long memberId, Long amount, Long missionHeartBalance, Long purchaseHeartBalance) {
        return new PurchaseHeartGained(memberId, amount, missionHeartBalance, purchaseHeartBalance);
    }

    private PurchaseHeartGained(Long memberId, Long amount, Long missionHeartBalance, Long purchaseHeartBalance) {
        this.memberId = memberId;
        this.amount = amount;
        this.missionHeartBalance = missionHeartBalance;
        this.purchaseHeartBalance = purchaseHeartBalance;
    }
}
