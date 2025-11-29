package deepple.deepple.heart.command.domain.heartusagepolicy;

import deepple.deepple.heart.command.domain.hearttransaction.vo.TransactionType;
import deepple.deepple.member.command.domain.member.Gender;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HeartUsagePolicyCommandRepository extends JpaRepository<HeartUsagePolicy, Long> {
    Optional<HeartUsagePolicy> findByGenderAndTransactionType(Gender gender, TransactionType transactionType);
}
