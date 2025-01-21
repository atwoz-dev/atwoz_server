package atwoz.atwoz.heartusagepolicy.infra;

import atwoz.atwoz.hearttransaction.domain.vo.TransactionType;
import atwoz.atwoz.heartusagepolicy.domain.HeartUsagePolicy;
import atwoz.atwoz.member.command.domain.member.Gender;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HeartUsagePolicyJpaRepository extends JpaRepository<HeartUsagePolicy, Long> {
    Optional<HeartUsagePolicy> findByGenderAndTransactionType(Gender gender, TransactionType transactionType);
}
