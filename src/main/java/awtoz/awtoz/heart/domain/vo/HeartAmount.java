package awtoz.awtoz.heart.domain.vo;

import awtoz.awtoz.heart.exception.InvalidHeartAmountException;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class HeartAmount {
    private static final Long MAX_USING_AMOUNT = 0L;
    private static final Long MIN_GAINING_AMOUNT = 1L;
    @Getter
    private final Long amount;

    protected HeartAmount() {
        this.amount = 0L;
    }

    public static HeartAmount createUsedAmount(Long amount) {
        validateUsingAmount(amount);
        return new HeartAmount(amount);
    }

    public static HeartAmount createGainedAmount(Long amount) {
        validateGainingAmount(amount);
        return new HeartAmount(amount);
    }

    public boolean isUsingAmount() {
        return this.amount <= MAX_USING_AMOUNT;
    }

    public boolean isGainingAmount() {
        return this.amount >= MIN_GAINING_AMOUNT;
    }

    private static void validateUsingAmount(Long amount) {
        if (amount > MAX_USING_AMOUNT) {
            throw new InvalidHeartAmountException("잘못된 하트 사용량 입니다. amount: " + amount);
        }
    }

    private static void validateGainingAmount(Long amount) {
        if (amount < MIN_GAINING_AMOUNT) {
            throw new InvalidHeartAmountException("잘못된 하트 획득량 입니다. amount: " + amount);
        }
    }
}
