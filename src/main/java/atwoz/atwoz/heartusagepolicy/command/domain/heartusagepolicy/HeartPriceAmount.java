package atwoz.atwoz.heartusagepolicy.command.domain.heartusagepolicy;

import atwoz.atwoz.heartusagepolicy.command.domain.heartusagepolicy.exception.InvalidHeartPriceAmountException;
import jakarta.persistence.Embeddable;
import lombok.NonNull;

@Embeddable
public class HeartPriceAmount {
    private static final Long MIN_PRICE = 1L;
    private final Long price;

    public static HeartPriceAmount from(Long price) {
        return new HeartPriceAmount(price);
    }

    public Long getAmount() {
        return -price;
    }

    public Long getPrice() {
        return price;
    }

    protected HeartPriceAmount() {
        this.price = MIN_PRICE;
    }

    private HeartPriceAmount(Long price) {
        validateMinPrice(price);
        this.price = price;
    }

    private void validateMinPrice(@NonNull Long price) {
        if (price < MIN_PRICE) {
            throw new InvalidHeartPriceAmountException("price 값이 최소값보다 낮습니다. price=" + price);
        }
    }
}
