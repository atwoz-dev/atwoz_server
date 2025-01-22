package atwoz.atwoz.heartusagepolicy.domain;

import atwoz.atwoz.hearttransaction.domain.vo.TransactionType;
import atwoz.atwoz.member.domain.member.Gender;

import java.util.Optional;

public interface HeartUsagePolicyRepository {
    Optional<HeartUsagePolicy> findByGenderAndTransactionType(Gender gender, TransactionType transactionType);
}
