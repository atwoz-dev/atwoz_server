package atwoz.atwoz.order.domain;

import atwoz.atwoz.common.domain.BaseEntity;
import atwoz.atwoz.order.exception.InvalidOrderStatusException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private Long heartPurchaseOptionId;

    @Enumerated
    @Column(columnDefinition = "varchar(50)")
    private PaymentMethod paymentMethod;

    @Enumerated
    @Column(columnDefinition = "varchar(50)")
    @Getter
    private OrderStatus status;

    public static Order of(Long memberId, Long heartPurchaseOptionId, PaymentMethod paymentMethod) {
        return new Order(memberId, heartPurchaseOptionId, paymentMethod);
    }

    public void pay() {
        if (!isPayable()) {
            throw new InvalidOrderStatusException("REQUESTED 상태의 주문만 결제할 수 있습니다. orderStatus=" + status);
        }
        setStatus(OrderStatus.PAID);
    }

    public void complete() {
        if (!isCompletable()) {
            throw new InvalidOrderStatusException("PAID 상태의 주문만 완료할 수 있습니다. orderStatus=" + status);
        }
        setStatus(OrderStatus.COMPLETED);
    }

    public void refund() {
        if (!isRefundable()) {
            throw new InvalidOrderStatusException("PAID, COMPLETED 상태의 주문만 환불할 수 있습니다. orderStatus=" + status);
        }
        setStatus(OrderStatus.REFUNDED);
    }

    public void cancel() {
        if (!isCancelable()) {
            throw new InvalidOrderStatusException("REQUESTED 상태의 주문만 취소할 수 있습니다. orderStatus=" + status);
        }
        setStatus(OrderStatus.CANCELED);
    }

    private Order(Long memberId, Long heartPurchaseOptionId, PaymentMethod paymentMethod) {
        setMemberId(memberId);
        setHeartPurchaseOptionId(heartPurchaseOptionId);
        setPaymentMethod(paymentMethod);
        setStatus(OrderStatus.REQUESTED);
    }

    private void setMemberId(@NonNull Long memberId) {
        this.memberId = memberId;
    }

    private void setHeartPurchaseOptionId(@NonNull Long heartPurchaseOptionId) {
        this.heartPurchaseOptionId = heartPurchaseOptionId;
    }

    private void setPaymentMethod(@NonNull PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    private void setStatus(@NonNull OrderStatus status) {
        this.status = status;
    }

    private boolean isPayable() {
        return status == OrderStatus.REQUESTED;
    }

    private boolean isCompletable() {
        return status == OrderStatus.PAID;
    }

    private boolean isRefundable() {
        return status == OrderStatus.COMPLETED || status == OrderStatus.PAID;
    }

    private boolean isCancelable() {
        return status == OrderStatus.REQUESTED;
    }
}
