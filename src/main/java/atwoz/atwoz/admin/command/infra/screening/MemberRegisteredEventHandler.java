package atwoz.atwoz.admin.command.infra.screening;

import atwoz.atwoz.admin.command.application.screening.ScreeningService;
import atwoz.atwoz.member.command.domain.member.event.MemberRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberRegisteredEventHandler {

    private final ScreeningService screeningService;

    @Async
    @TransactionalEventListener(value = MemberRegisteredEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handle(MemberRegisteredEvent event) {
        try {
            screeningService.create(event.getMemberId());
            log.info("[심사 생성] member id: {}", event.getMemberId());
        } catch (Exception e) {
            log.error("Member(id: {})의 심사 생성 중 예외 발생", event.getMemberId(), e);
        }
    }
}