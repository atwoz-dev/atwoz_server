package atwoz.atwoz.heartusagepolicy.infra;

import atwoz.atwoz.hearttransaction.domain.vo.TransactionType;
import atwoz.atwoz.heartusagepolicy.domain.HeartUsagePolicy;
import atwoz.atwoz.heartusagepolicy.domain.HeartUsagePolicyRepository;
import atwoz.atwoz.member.domain.member.Gender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class HeartUsagePolicyRepositoryImpl implements HeartUsagePolicyRepository {
    private final HeartUsagePolicyJpaRepository heartUsagePolicyJpaRepository;

    @Override
    public Optional<HeartUsagePolicy> findByGenderAndTransactionType(Gender gender, TransactionType transactionType) {
        return heartUsagePolicyJpaRepository.findByGenderAndTransactionType(gender, transactionType);
    }
}
