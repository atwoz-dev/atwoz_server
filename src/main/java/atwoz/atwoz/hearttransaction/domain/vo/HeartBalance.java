package atwoz.atwoz.hearttransaction.domain.vo;

import atwoz.atwoz.hearttransaction.exception.InvalidHeartAmountException;
import atwoz.atwoz.hearttransaction.exception.InvalidHeartBalanceException;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Embeddable
@EqualsAndHashCode
public final class HeartBalance {
    private static final Long MIN_HEART_BALANCE = 0L;
    @Getter
    private final Long purchaseHeartBalance;
    @Getter
    private final Long missionHeartBalance;

    public static HeartBalance init() {
        return new HeartBalance();
    }

    public HeartBalance useHeart(HeartAmount heartChangeAmount) {
        validateUsingAmount(heartChangeAmount);
        validateBalanceIsUsable(heartChangeAmount);
        Long purchaseHeartBalanceAfterUsing = usePurchaseHeart(heartChangeAmount);
        HeartAmount remainingHeartChangeAmount = calculateRemainingHeartChangeAmount(heartChangeAmount, purchaseHeartBalanceAfterUsing);
        Long missionHeartBalanceAfterUsing = useMissionHeart(remainingHeartChangeAmount);
        return new HeartBalance(purchaseHeartBalanceAfterUsing, missionHeartBalanceAfterUsing);
    }

    public HeartBalance gainPurchaseHeart(HeartAmount heartChangeAmount) {
        validateGainingAmount(heartChangeAmount);
        Long purchaseHeartBalanceAfterGaining = this.purchaseHeartBalance + heartChangeAmount.getAmount();
        return new HeartBalance(purchaseHeartBalanceAfterGaining, this.missionHeartBalance);
    }

    public HeartBalance gainMissionHeart(HeartAmount heartChangeAmount) {
        validateGainingAmount(heartChangeAmount);
        Long missionHeartBalanceAfterGaining = this.missionHeartBalance + heartChangeAmount.getAmount();
        return new HeartBalance(this.purchaseHeartBalance, missionHeartBalanceAfterGaining);
    }

    protected HeartBalance() {
        this.purchaseHeartBalance = MIN_HEART_BALANCE;
        this.missionHeartBalance = MIN_HEART_BALANCE;
    }

    private void validateBalanceIsUsable(HeartAmount heartChangeAmount) {
        Long totalHeartBalance = this.purchaseHeartBalance + this.missionHeartBalance;
        if (totalHeartBalance + heartChangeAmount.getAmount() < MIN_HEART_BALANCE) {
            throw new InvalidHeartBalanceException("하트 잔액이 부족합니다. totalHeartBalance: " + totalHeartBalance + ", amount: " + heartChangeAmount.getAmount());
        }
    }

    private HeartBalance(Long purchaseHeartBalance, Long missionHeartBalance) {
        validateMinHeartBalance(purchaseHeartBalance, missionHeartBalance);
        this.purchaseHeartBalance = purchaseHeartBalance;
        this.missionHeartBalance = missionHeartBalance;
    }

    private void validateMinHeartBalance(Long purchaseHeartBalance, Long missionHeartBalance) {
        if (purchaseHeartBalance < MIN_HEART_BALANCE) {
            throw new InvalidHeartBalanceException("하트 잔액이 최소값 미만입니다. purchaseHeartBalance: " + purchaseHeartBalance);
        }
        if (missionHeartBalance < MIN_HEART_BALANCE) {
            throw new InvalidHeartBalanceException("하트 잔액이 최소값 미만입니다. missionHeartBalance: " + missionHeartBalance);
        }
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

    private void validateUsingAmount(HeartAmount heartChangeAmount) {
        if (!heartChangeAmount.isUsingAmount()) {
            throw new InvalidHeartAmountException("잘못된 하트 사용량 입니다. amount: " + heartChangeAmount.getAmount());
        }
    }

    private void validateGainingAmount(HeartAmount heartChangeAmount) {
        if (!heartChangeAmount.isGainingAmount()) {
            throw new InvalidHeartAmountException("잘못된 하트 획득량 입니다. amount: " + heartChangeAmount.getAmount());
        }
    }
}