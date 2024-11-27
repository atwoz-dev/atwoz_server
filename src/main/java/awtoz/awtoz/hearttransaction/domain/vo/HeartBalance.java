package awtoz.awtoz.hearttransaction.domain.vo;

import awtoz.awtoz.hearttransaction.exception.InvalidHeartBalanceException;
import jakarta.persistence.Embeddable;

@Embeddable
public final class HeartBalance {
    private static final Long MIN_HEART_BALANCE = 0L;
    private final Long purchaseHeartBalance;
    private final Long missionHeartBalance;

    public HeartBalance useHeart(HeartAmount heartChangeAmount) {
        validateBalanceIsUsable(heartChangeAmount);
        Long purchaseHeartBalanceAfterUsing = usePurchaseHeart(heartChangeAmount);
        HeartAmount remainingHeartChangeAmount = calculateRemainingHeartChangeAmount(heartChangeAmount, purchaseHeartBalanceAfterUsing);
        Long missionHeartBalanceAfterUsing = useMissionHeart(heartChangeAmount);
        return new HeartBalance(purchaseHeartBalanceAfterUsing, missionHeartBalanceAfterUsing);
    }

    public HeartBalance gainPurchaseHeart(HeartAmount heartChangeAmount) {
        Long purchaseHeartBalanceAfterGaining = this.purchaseHeartBalance + heartChangeAmount.getAmount();
        return new HeartBalance(purchaseHeartBalanceAfterGaining, this.missionHeartBalance);
    }

    public HeartBalance gainMissionHeart(HeartAmount heartChangeAmount) {
        Long missionHeartBalanceAfterGaining = this.missionHeartBalance + heartChangeAmount.getAmount();
        return new HeartBalance(this.purchaseHeartBalance, missionHeartBalanceAfterGaining);
    }

    protected HeartBalance() {
        this.purchaseHeartBalance = MIN_HEART_BALANCE;
        this.missionHeartBalance = MIN_HEART_BALANCE;
    }

    private HeartBalance(Long purchaseHeartBalance, Long missionHeartBalance) {
        validateMinHeartBalance(purchaseHeartBalance, missionHeartBalance);
        this.purchaseHeartBalance = purchaseHeartBalance;
        this.missionHeartBalance = missionHeartBalance;
    }

    private Long usePurchaseHeart(HeartAmount heartChangeAmount) {
        return Math.max(this.purchaseHeartBalance + heartChangeAmount.getAmount(), 0L);
    }

    private HeartAmount calculateRemainingHeartChangeAmount(HeartAmount heartChangeAmount, Long purchaseHeartBalanceAfterUsing) {
        Long usedPurchaseHeart = this.purchaseHeartBalance - purchaseHeartBalanceAfterUsing;
        Long remainingHeartChangeAmount = heartChangeAmount.getAmount() + usedPurchaseHeart;
        return HeartAmount.from(remainingHeartChangeAmount);
    }

    private Long useMissionHeart(HeartAmount heartChangeAmount) {
        return Math.max(this.missionHeartBalance + heartChangeAmount.getAmount(), 0L);
    }

    private void validateMinHeartBalance(Long purchaseHeartBalance, Long missionHeartBalance) {
        if (purchaseHeartBalance < MIN_HEART_BALANCE) {
            throw new InvalidHeartBalanceException("하트 잔액이 최소값 미만입니다. purchaseHeartBalance: " + purchaseHeartBalance);
        }
        if (missionHeartBalance < MIN_HEART_BALANCE) {
            throw new InvalidHeartBalanceException("하트 잔액이 최소값 미만입니다. missionHeartBalance: " + missionHeartBalance);
        }
    }

    private void validateBalanceIsUsable(HeartAmount heartChangeAmount) {
        Long totalHeartBalance = this.purchaseHeartBalance + this.missionHeartBalance;
        if (totalHeartBalance + heartChangeAmount.getAmount() < MIN_HEART_BALANCE) {
            throw new InvalidHeartBalanceException("하트 잔액이 부족합니다. totalHeartBalance: " + totalHeartBalance + ", amount: " + amount);
        }
    }
}