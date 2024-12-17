package atwoz.atwoz.heartusagepolicy.domain;

import atwoz.atwoz.hearttransaction.domain.HeartTransaction;
import atwoz.atwoz.hearttransaction.domain.HeartTransactionRepository;
import atwoz.atwoz.hearttransaction.domain.vo.HeartAmount;
import atwoz.atwoz.hearttransaction.domain.vo.HeartBalance;
import atwoz.atwoz.hearttransaction.domain.vo.TransactionType;
import atwoz.atwoz.heartusagepolicy.application.HeartUsageService;
import atwoz.atwoz.heartusagepolicy.exception.HeartUsagePolicyNotFoundException;
import atwoz.atwoz.member.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HeartUsageServiceImpl implements HeartUsageService {
    private final HeartUsagePolicyRepository heartUsagePolicyRepository;
    private final HeartTransactionRepository heartTransactionRepository;

    @Override
    @Transactional
    public HeartTransaction useHeart(Member member, TransactionType transactionType) {
        HeartAmount heartAmount = getHeartAmount(member, transactionType);
        HeartBalance balanceAfterUsingHeart = deductHeartBalance(member, heartAmount);
        return createHeartTransaction(member, transactionType, heartAmount, balanceAfterUsingHeart);
    }

    private HeartAmount getHeartAmount(Member member, TransactionType transactionType) {
        HeartUsagePolicy heartUsagePolicy = heartUsagePolicyRepository.findByGenderAndTransactionType(member.getGender(), transactionType)
                .orElseThrow(() -> new HeartUsagePolicyNotFoundException("해당하는 하트 사용 정책이 없습니다. gender: " + member.getGender() + ", transactionType: " + transactionType));
        if (member.isVipMember()) {
            return HeartAmount.from(0L);
        }
        HeartAmount heartAmount = HeartAmount.from(heartUsagePolicy.getAmount());
        return heartAmount;
    }

    private HeartBalance deductHeartBalance(Member member, HeartAmount heartAmount) {
        member.useHeart(heartAmount);
        HeartBalance balanceAfterUsingHeart = member.getHeartBalance();
        return balanceAfterUsingHeart;
    }

    private HeartTransaction createHeartTransaction(Member member, TransactionType transactionType, HeartAmount heartAmount, HeartBalance balanceAfterUsingHeart) {
        HeartTransaction heartTransaction = HeartTransaction.of(member.getId(), transactionType, heartAmount, balanceAfterUsingHeart);
        return heartTransactionRepository.save(heartTransaction);
    }
}
