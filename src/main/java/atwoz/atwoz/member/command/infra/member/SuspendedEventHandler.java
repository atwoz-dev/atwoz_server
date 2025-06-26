package atwoz.atwoz.member.command.infra.member;

import atwoz.atwoz.admin.command.domain.suspension.MemberSuspendedEvent;
import atwoz.atwoz.admin.command.domain.suspension.MemberUnsuspendedEvent;
import atwoz.atwoz.member.command.application.member.MemberProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@Slf4j
@RequiredArgsConstructor
public class SuspendedEventHandler {

    private final MemberProfileService memberProfileService;

    @Async
    @TransactionalEventListener(value = MemberSuspendedEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handle(MemberSuspendedEvent event) {
        try {
            memberProfileService.changeMemberActivityStatus(event.getMemberId(), event.getStatus());
        } catch (Exception e) {
            log.error("Member(id: {})의 프로필 업데이트 중 예외가 발생했습니다.", event.getMemberId(), e);
        }
    }

    @Async
    @TransactionalEventListener(value = MemberUnsuspendedEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handle(MemberUnsuspendedEvent event) {
        try {
            memberProfileService.changeMemberActivityStatus(event.getMemberId(), "ACTIVE");
        } catch (Exception e) {
            log.error("Member(id: {})의 프로필 업데이트 중 예외가 발생했습니다.", event.getMemberId(), e);
        }
    }
}
