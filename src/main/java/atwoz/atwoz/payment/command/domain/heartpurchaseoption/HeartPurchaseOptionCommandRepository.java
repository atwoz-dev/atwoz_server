package atwoz.atwoz.payment.command.domain.heartpurchaseoption;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HeartPurchaseOptionCommandRepository extends JpaRepository<HeartPurchaseOption, Long> {
    Optional<HeartPurchaseOption> findById(Long id);

    Optional<HeartPurchaseOption> findByProductId(String productId);

    boolean existsByProductId(String productId);
}
