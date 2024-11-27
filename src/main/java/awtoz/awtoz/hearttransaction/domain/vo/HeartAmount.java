package awtoz.awtoz.hearttransaction.domain.vo;

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

    public static HeartAmount from(Long amount) {
        return new HeartAmount(amount);
    }

    public boolean isUsingAmount() {
        return this.amount <= MAX_USING_AMOUNT;
    }

    public boolean isGainingAmount() {
        return this.amount >= MIN_GAINING_AMOUNT;
    }
}
