package atwoz.atwoz.heart.command.application.heartusagepolicy;

import atwoz.atwoz.heart.command.application.heartusagepolicy.exception.HeartUsagePolicyNotFoundException;
import atwoz.atwoz.heart.command.domain.hearttransaction.HeartTransaction;
import atwoz.atwoz.heart.command.domain.hearttransaction.HeartTransactionCommandRepository;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.HeartAmount;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.HeartBalance;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.TransactionSubtype;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.TransactionType;
import atwoz.atwoz.heart.command.domain.heartusagepolicy.HeartUsagePolicy;
import atwoz.atwoz.heart.command.domain.heartusagepolicy.HeartUsagePolicyCommandRepository;
import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HeartUsagePolicyService {
    private final HeartUsagePolicyCommandRepository heartUsagePolicyCommandRepository;
    private final HeartTransactionCommandRepository heartTransactionCommandRepository;
    private final MemberCommandRepository memberCommandRepository;

    @Transactional
    public HeartTransaction useHeart(long memberId, TransactionType transactionType, String content,
        String transactionSubtype) {
        Member member = getMember(memberId);
        HeartAmount heartAmount = getHeartAmount(member, transactionType, transactionSubtype);
        HeartBalance balanceAfterUsingHeart = deductHeartBalance(member, heartAmount);
        return createHeartTransaction(member, transactionType, content, heartAmount, balanceAfterUsingHeart);
    }

    private Member getMember(long memberId) {
        return memberCommandRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new);
    }

    private HeartAmount getHeartAmount(Member member, TransactionType transactionType, String transactionSubtype) {
        HeartUsagePolicy heartUsagePolicy = heartUsagePolicyCommandRepository.findByGenderAndTransactionType(
                member.getGender(), transactionType)
            .orElseThrow(HeartUsagePolicyNotFoundException::new);
        TransactionSubtype subtype = TransactionSubtype.valueOf(transactionSubtype);
        return HeartAmount.from(heartUsagePolicy.getAmount(member.isVip(), subtype));
    }

    private HeartBalance deductHeartBalance(Member member, HeartAmount heartAmount) {
        member.useHeart(heartAmount);
        HeartBalance balanceAfterUsingHeart = member.getHeartBalance();
        return balanceAfterUsingHeart;
    }

    private HeartTransaction createHeartTransaction(Member member, TransactionType transactionType, String content,
        HeartAmount heartAmount, HeartBalance balanceAfterUsingHeart) {
        HeartTransaction heartTransaction = HeartTransaction.of(member.getId(), transactionType, content, heartAmount,
            balanceAfterUsingHeart);
        return heartTransactionCommandRepository.save(heartTransaction);
    }
}
