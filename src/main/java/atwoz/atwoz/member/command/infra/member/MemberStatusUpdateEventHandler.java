package atwoz.atwoz.member.command.infra.member;

import atwoz.atwoz.admin.command.domain.screening.event.ScreeningApprovedEvent;
import atwoz.atwoz.admin.command.domain.screening.event.ScreeningRejectedEvent;
import atwoz.atwoz.member.command.application.member.MemberProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberStatusUpdateEventHandler {

    private MemberProfileService memberProfileService;

    @EventListener(value = ScreeningApprovedEvent.class)
    public void handle(ScreeningApprovedEvent event) {
    }

    @EventListener(value = ScreeningRejectedEvent.class)
    public void handle(ScreeningRejectedEvent event) {

    }
}
