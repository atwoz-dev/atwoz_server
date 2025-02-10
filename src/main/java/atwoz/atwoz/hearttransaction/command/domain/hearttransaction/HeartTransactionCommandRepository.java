package atwoz.atwoz.hearttransaction.command.domain.hearttransaction;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HeartTransactionCommandRepository extends JpaRepository<HeartTransaction, Long> {
    HeartTransaction save(HeartTransaction heartTransaction);
}
