package atwoz.atwoz.notification.infra.notificationsetting;

import atwoz.atwoz.member.command.domain.member.event.MemberRegisteredEvent;
import atwoz.atwoz.notification.command.application.DuplicateNotificationPreferenceException;
import atwoz.atwoz.notification.command.application.NotificationPreferenceService;
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

    private final NotificationPreferenceService notificationPreferenceService;

    @Async
    @TransactionalEventListener(value = MemberRegisteredEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handle(MemberRegisteredEvent event) {
        try {
            notificationPreferenceService.create(event.getMemberId());
        } catch (DuplicateNotificationPreferenceException e) {
            log.warn(e.getMessage());
        } catch (Exception e) {
            log.error("Member(memberId: {})의 NotificationPreference 생성 중 예외가 발생습니다.", event.getMemberId(), e);
        }
    }
}
