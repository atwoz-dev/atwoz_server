package atwoz.atwoz.admin.infra;

import atwoz.atwoz.admin.command.application.screening.DuplicateScreeningException;
import atwoz.atwoz.admin.command.application.screening.ScreeningService;
import atwoz.atwoz.member.command.domain.member.event.MemberProfileInitializedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScreeningEventHandler {
    private final ScreeningService screeningService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(MemberProfileInitializedEvent event) {
        try {
            screeningService.create(event.getMemberId());
        } catch (DuplicateScreeningException e) {
            log.warn(e.getMessage());
        } catch (Exception e) {
            log.error("Member(id: {})의 Screening 생성 중 예외가 발생했습니다.", event.getMemberId(), e);
        }
    }
}
