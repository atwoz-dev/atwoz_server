package atwoz.atwoz.payment.command.domain.heartpurchaseoption;

import atwoz.atwoz.payment.command.domain.heartpurchaseoption.exception.InvalidPriceException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Embeddable
@EqualsAndHashCode
public class Price {
    private static final Long MIN_PRICE = 1L;

    @Getter
    @Column(name = "price")
    private final Long value;

    public static Price from(Long value) {
        return new Price(value);
    }

    protected Price() {
        this.value = MIN_PRICE;
    }

    private Price(@NonNull Long value) {
        validateMinPrice(value);
        this.value = value;
    }

    private void validateMinPrice(Long value) {
        if (value < MIN_PRICE) {
            throw new InvalidPriceException("price 값이 최소값보다 낮습니다. price=" + value);
        }
    }
}
