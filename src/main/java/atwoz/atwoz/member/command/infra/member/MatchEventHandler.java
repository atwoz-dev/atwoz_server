package atwoz.atwoz.member.command.infra.member;

import atwoz.atwoz.match.command.domain.match.event.MatchRequestedEvent;
import atwoz.atwoz.match.command.domain.match.event.MatchRespondedEvent;
import atwoz.atwoz.member.command.application.member.MemberProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchEventHandler {

    private final MemberProfileService memberProfileService;

    @EventListener(value = MatchRequestedEvent.class)
    public void handle(MatchRequestedEvent event) {
        memberProfileService.validatePrimaryContactTypeSetting(event.getRequesterId());
    }

    @EventListener(value = MatchRespondedEvent.class)
    public void handle(MatchRespondedEvent event) {
        memberProfileService.validatePrimaryContactTypeSetting(event.getRequesterId());
    }
}