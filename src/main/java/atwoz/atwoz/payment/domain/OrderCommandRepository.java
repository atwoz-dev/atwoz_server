package atwoz.atwoz.payment.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderCommandRepository extends JpaRepository<Order, Long> {
    boolean existsByTransactionIdAndPaymentMethod(String transactionId, PaymentMethod paymentMethod);
}
