package atwoz.atwoz.payment.command.domain.heartpurchaseoption;

import atwoz.atwoz.common.entity.SoftDeleteBaseEntity;
import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.payment.command.domain.heartpurchaseoption.exception.InvalidHeartPurchaseOptionException;
import atwoz.atwoz.payment.command.domain.order.event.HeartPurchasedEvent;
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

    private HeartPurchaseOption(HeartPurchaseAmount amount, Price price, String productId, String name) {
        setAmount(amount);
        setPrice(price);
        setProductId(productId);
        setName(name);
    }

    public static HeartPurchaseOption of(HeartPurchaseAmount amount, Price price, String productId, String name) {
        return new HeartPurchaseOption(amount, price, productId, name);
    }

    public Long getHeartAmount() {
        return amount.getAmount();
    }

    public void purchase(@NonNull Long memberId, @NonNull Integer quantity) {
        Long heartAmount = getHeartAmount() * quantity;
        Events.raise(HeartPurchasedEvent.of(memberId, heartAmount));
    }

    private void setAmount(@NonNull HeartPurchaseAmount amount) {
        this.amount = amount;
    }

    private void setPrice(@NonNull Price price) {
        this.price = price;
    }

    private void setProductId(@NonNull String productId) {
        if (productId.isBlank()) {
            throw new InvalidHeartPurchaseOptionException("productId 값이 비어있습니다.");
        }
        this.productId = productId;
    }

    private void setName(@NonNull String name) {
        if (name.isBlank()) {
            throw new InvalidHeartPurchaseOptionException("name 값이 비어있습니다.");
        }
        this.name = name;
    }
}
