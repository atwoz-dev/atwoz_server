package atwoz.atwoz.admin.command.application.selfintroduction;

import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.community.command.domain.selfintroduction.event.SelfIntroductionOpenStatusChangeEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminSelfIntroductionCommandService {

    @Transactional
    public void convertToClose(Long selfIntroductionId) {
        Events.raise(SelfIntroductionOpenStatusChangeEvent.of(selfIntroductionId, true));
    }

    @Transactional
    public void convertToOpen(Long selfIntroductionId) {
        Events.raise(SelfIntroductionOpenStatusChangeEvent.of(selfIntroductionId, false));
    }
}
