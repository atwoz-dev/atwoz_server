package atwoz.atwoz.heartusagepolicy.domain;

import atwoz.atwoz.heartusagepolicy.exception.InvalidHeartPriceAmountException;
import jakarta.persistence.Embeddable;

@Embeddable
public class HeartPriceAmount {
    private static final Long MIN_PRICE = 1L;
    private final Long price;

    public static HeartPriceAmount from(Long price) {
        return new HeartPriceAmount(price);
    }

    protected HeartPriceAmount() {
        this.price = MIN_PRICE;
    }

    private HeartPriceAmount(Long price) {
        validateMinPrice(price);
        this.price = price;
    }

    private void validateMinPrice(Long price) {
        if (price < MIN_PRICE) {
            throw new InvalidHeartPriceAmountException("price 값이 최소값보다 낮습니다. price=" + price);
        }
    }
}
