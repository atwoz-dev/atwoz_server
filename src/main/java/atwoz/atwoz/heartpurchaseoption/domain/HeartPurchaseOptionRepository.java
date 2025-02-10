package atwoz.atwoz.heartpurchaseoption.domain;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HeartPurchaseOptionRepository extends JpaRepository<HeartPurchaseOption, Long> {
    Optional<HeartPurchaseOption> findById(Long id);
    Optional<HeartPurchaseOption> findByProductId(String productId);
}
