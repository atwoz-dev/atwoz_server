package atwoz.atwoz.community.command.infra.selfintroduction;


import atwoz.atwoz.community.command.application.selfintroduction.SelfIntroductionService;
import atwoz.atwoz.community.command.domain.selfintroduction.event.SelfIntroductionOpenStatusChangeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SelfIntroductionEventHandler {

    private final SelfIntroductionService selfIntroductionService;

    @EventListener(value = SelfIntroductionOpenStatusChangeEvent.class)
    public void handle(SelfIntroductionOpenStatusChangeEvent event) {
        selfIntroductionService.changeOpenStatus(event.getId(), event.isOpen());
    }
}
