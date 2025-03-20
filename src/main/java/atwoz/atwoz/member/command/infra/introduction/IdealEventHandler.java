package atwoz.atwoz.member.command.infra.introduction;

import atwoz.atwoz.member.command.application.introduction.MemberIdealService;
import atwoz.atwoz.notification.command.infra.notificationsetting.MemberRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdealEventHandler {
    private final MemberIdealService memberIdealService;

    @Async
    @TransactionalEventListener(value = MemberRegisteredEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handle(MemberRegisteredEvent event) {
        try {
            memberIdealService.init(event.getMemberId());
        } catch (Exception e) {
            log.error("Member(memberId: {})의 Ideal 생성 중 예외가 발생합니다.", event.getMemberId(), e);
        }
    }
}
