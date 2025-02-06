package atwoz.atwoz.heartpurchaseoption.domain;

import atwoz.atwoz.common.entity.SoftDeleteBaseEntity;
import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.heartpurchaseoption.exception.InvalidHeartPurchaseOptionException;
import atwoz.atwoz.payment.domain.event.HeartPurchased;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "heart_purchase_options")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HeartPurchaseOption extends SoftDeleteBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Embedded
    private HeartPurchaseAmount amount;

    @Embedded
    private Price price;

    private String name;

    private String productId;

    public static HeartPurchaseOption of(HeartPurchaseAmount amount, Price price, String name) {
        return new HeartPurchaseOption(amount, price, name);
    }

    public Long getHeartAmount () {
        return amount.getAmount();
    }

    public void purchase(@NonNull Long memberId, @NonNull Integer quantity) {
        Long heartAmount = getHeartAmount() * quantity;
        Events.raise(HeartPurchased.of(memberId, heartAmount));
    }

    private HeartPurchaseOption(HeartPurchaseAmount amount, Price price, String name) {
        setAmount(amount);
        setPrice(price);
        setName(name);
    }

    private void setAmount(@NonNull HeartPurchaseAmount amount) {
        this.amount = amount;
    }

    private void setPrice(@NonNull Price price) {
        this.price = price;
    }

    private void setName(@NonNull String name) {
        if (name.isBlank()) {
            throw new InvalidHeartPurchaseOptionException("name 값이 비어있습니다.");
        }
        this.name = name;
    }
}
