package atwoz.atwoz.hearttransaction.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HeartTransactionCommandRepository extends JpaRepository<HeartTransaction, Long> {
    HeartTransaction save(HeartTransaction heartTransaction);
}
