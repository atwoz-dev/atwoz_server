package atwoz.atwoz.notification.command.infra.notificationsetting;

import atwoz.atwoz.notification.command.application.notifiactionsetting.DuplicateNotificationSettingException;
import atwoz.atwoz.notification.command.application.notifiactionsetting.NotificationSettingService;
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

    private final NotificationSettingService notificationSettingService;

    @Async
    @TransactionalEventListener(value = MemberRegisteredEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handle(MemberRegisteredEvent event) {
        try {
            notificationSettingService.create(event.getMemberId());
        } catch (DuplicateNotificationSettingException e) {
            log.warn(e.getMessage());
        } catch (Exception e) {
            log.error("NotificationSetting 생성 중 예외 발생 (memberId: {})", event.getMemberId(), e);
        }
    }
}
