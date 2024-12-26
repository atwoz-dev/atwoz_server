package atwoz.atwoz.heartpurchaseoption.domain;

import atwoz.atwoz.common.domain.SoftDeleteBaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HeartPurchaseOption extends SoftDeleteBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private HeartPurchaseAmount amount;

    @Embedded
    private Price price;

    private String name;

    public static HeartPurchaseOption of(HeartPurchaseAmount amount, Price price, String name) {
        return new HeartPurchaseOption(amount, price, name);
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
            throw new IllegalArgumentException("name 값이 비어있습니다.");
        }
        this.name = name;
    }
}
