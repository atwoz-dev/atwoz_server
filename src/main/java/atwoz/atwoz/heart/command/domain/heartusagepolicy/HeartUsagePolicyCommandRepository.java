package atwoz.atwoz.heart.command.domain.heartusagepolicy;

import atwoz.atwoz.heart.command.domain.hearttransaction.vo.TransactionType;
import atwoz.atwoz.member.command.domain.member.Gender;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HeartUsagePolicyCommandRepository extends JpaRepository<HeartUsagePolicy, Long> {
    Optional<HeartUsagePolicy> findByGenderAndTransactionType(Gender gender, TransactionType transactionType);
}
