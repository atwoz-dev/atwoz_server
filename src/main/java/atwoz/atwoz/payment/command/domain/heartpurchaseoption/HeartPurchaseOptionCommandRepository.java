package atwoz.atwoz.payment.command.domain.heartpurchaseoption;


import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HeartPurchaseOptionCommandRepository extends JpaRepository<HeartPurchaseOption, Long> {
    Optional<HeartPurchaseOption> findById(Long id);

    Optional<HeartPurchaseOption> findByProductId(String productId);

    boolean existsByProductId(@NotBlank(message = "상품 ID를 입력해주세요.") String s);
}
