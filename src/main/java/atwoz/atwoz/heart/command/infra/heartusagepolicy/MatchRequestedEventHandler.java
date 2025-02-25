package atwoz.atwoz.heart.command.infra.heartusagepolicy;

import atwoz.atwoz.heart.command.application.heartusagepolicy.HeartUsagePolicyService;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.TransactionType;
import atwoz.atwoz.match.command.domain.match.event.MatchRequestedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchRequestedEventHandler {
    private final HeartUsagePolicyService heartUsageService;

    @EventListener(value = MatchRequestedEvent.class)
    public void handle(MatchRequestedEvent event) {
        heartUsageService.useHeart(event.getRequesterId(), TransactionType.MESSAGE);
    }
}
