package atwoz.atwoz.payment.command.domain.heartpurchaseoption;

import atwoz.atwoz.payment.command.domain.heartpurchaseoption.exception.InvalidHeartPurchaseAmountException;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Embeddable
@EqualsAndHashCode
public class HeartPurchaseAmount {
    private static final Long MIN_AMOUNT = 1L;

    @Getter
    private final Long amount;

    public static HeartPurchaseAmount from(Long amount) {
        return new HeartPurchaseAmount(amount);
    }

    private HeartPurchaseAmount(@NonNull Long amount) {
        validateMinAmount(amount);
        this.amount = amount;
    }

    private void validateMinAmount(Long amount) {
        if (amount < MIN_AMOUNT) {
            throw new InvalidHeartPurchaseAmountException("amount 값이 최소값보다 낮습니다. amount=" + amount);
        }
    }

    protected HeartPurchaseAmount() {
        this.amount = MIN_AMOUNT;
    }
}
