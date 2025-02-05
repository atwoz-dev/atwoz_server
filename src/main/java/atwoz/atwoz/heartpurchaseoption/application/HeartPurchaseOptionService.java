package atwoz.atwoz.heartpurchaseoption.application;

import atwoz.atwoz.heartpurchaseoption.application.exception.HeartPurchaseOptionNotFoundException;
import atwoz.atwoz.heartpurchaseoption.application.exception.MemberNotFoundException;
import atwoz.atwoz.heartpurchaseoption.domain.HeartPurchaseOption;
import atwoz.atwoz.heartpurchaseoption.domain.HeartPurchaseOptionRepository;
import atwoz.atwoz.hearttransaction.domain.HeartTransaction;
import atwoz.atwoz.hearttransaction.domain.HeartTransactionRepository;
import atwoz.atwoz.hearttransaction.domain.vo.HeartAmount;
import atwoz.atwoz.hearttransaction.domain.vo.HeartBalance;
import atwoz.atwoz.hearttransaction.domain.vo.TransactionType;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HeartPurchaseOptionService {
    private final MemberCommandRepository memberRepository;
    private final HeartPurchaseOptionRepository heartPurchaseOptionRepository;
    private final HeartTransactionRepository heartTransactionRepository;

    public void grantPurchasedHearts(String productId, int quantity, Long memberId) {
        Member member = getMemberById(memberId);
        HeartAmount heartAmount = calculateHeartAmount(productId, quantity);
        HeartBalance balanceAfterPurchasingHeart = addHeartBalance(member, heartAmount);
        createHeartTransaction(member, heartAmount, balanceAfterPurchasingHeart);
    }

    private Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("멤버가 존재하지 않습니다. member id:" + memberId));
    }

    private HeartAmount calculateHeartAmount(String productId, int quantity) {
        HeartPurchaseOption heartPurchaseOption = heartPurchaseOptionRepository.findByProductId(productId)
                .orElseThrow(() -> new HeartPurchaseOptionNotFoundException("하트 구매 옵션이 존재하지 않습니다. product id:" + productId));
        HeartAmount heartAmount = HeartAmount.from(heartPurchaseOption.getHeartAmount() * quantity);
        return heartAmount;
    }

    private HeartBalance addHeartBalance(Member member, HeartAmount heartAmount) {
        member.gainPurchaseHeart(heartAmount);
        HeartBalance balanceAfterPurchasingHeart = member.getHeartBalance();
        return balanceAfterPurchasingHeart;
    }

    private void createHeartTransaction(Member member, HeartAmount heartAmount, HeartBalance balanceAfterPurchasingHeart) {
        HeartTransaction heartTransaction = HeartTransaction.of(member.getId(), TransactionType.PURCHASE, heartAmount, balanceAfterPurchasingHeart);
        heartTransactionRepository.save(heartTransaction);
    }
}
