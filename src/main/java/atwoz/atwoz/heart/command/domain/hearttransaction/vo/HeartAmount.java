package atwoz.atwoz.heart.command.domain.hearttransaction.vo;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Embeddable
@EqualsAndHashCode
public final class HeartAmount {
    private static final Long MAX_USING_AMOUNT = 0L;
    private static final Long MIN_GAINING_AMOUNT = 1L;
    @Getter
    private final Long amount;

    protected HeartAmount() {
        this.amount = 0L;
    }

    private HeartAmount(@NonNull Long amount) {
        this.amount = amount;
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

    public boolean isZero() {
        return this.amount.equals(0L);
    }
}
