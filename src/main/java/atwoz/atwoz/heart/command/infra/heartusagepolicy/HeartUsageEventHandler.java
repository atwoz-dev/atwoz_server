package atwoz.atwoz.heart.command.infra.heartusagepolicy;

import atwoz.atwoz.heart.command.application.heartusagepolicy.HeartUsagePolicyService;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.TransactionType;
import atwoz.atwoz.match.command.domain.match.event.MatchRequestedEvent;
import atwoz.atwoz.member.command.domain.introduction.event.MemberIntroducedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HeartUsageEventHandler {
    private final HeartUsagePolicyService heartUsageService;

    @EventListener(value = MatchRequestedEvent.class)
    public void handle(MatchRequestedEvent event) {
        heartUsageService.useHeart(event.getRequesterId(), TransactionType.MESSAGE,
            TransactionType.MESSAGE.getDescription(), event.getMatchType());
    }

    @EventListener(value = MemberIntroducedEvent.class)
    public void handle(MemberIntroducedEvent event) {
        heartUsageService.useHeart(event.getMemberId(), TransactionType.INTRODUCTION, event.getContent(),
            event.getIntroductionType());
    }
}
