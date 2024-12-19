package atwoz.atwoz.hearttransaction.infra;

import atwoz.atwoz.hearttransaction.domain.HeartTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeartTransactionJpaRepository extends JpaRepository<HeartTransaction, Long> {
    HeartTransaction save(HeartTransaction heartTransaction);
}
