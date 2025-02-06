package atwoz.atwoz.member.command.application.member;

import atwoz.atwoz.hearttransaction.domain.vo.HeartAmount;
import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberHeartBalanceService {
    private final MemberCommandRepository memberCommandRepository;

    public void grantPurchasedHearts(Long memberId, Long amount) {
        Member member = getMemberById(memberId);
        HeartAmount heartAmount = HeartAmount.from(amount);
        member.gainPurchaseHeart(heartAmount);
    }

    private Member getMemberById(Long memberId) {
        return memberCommandRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException());
    }
}
