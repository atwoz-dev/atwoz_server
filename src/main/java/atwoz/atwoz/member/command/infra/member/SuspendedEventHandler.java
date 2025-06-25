package atwoz.atwoz.member.command.infra.member;

import atwoz.atwoz.admin.command.domain.suspension.MemberSuspendedEvent;
import atwoz.atwoz.member.command.application.member.MemberProfileService;
import atwoz.atwoz.member.command.domain.member.ActivityStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@Slf4j
@RequiredArgsConstructor
public class SuspendedEventHandler {

    private final MemberProfileService memberProfileService;

    @TransactionalEventListener(value = MemberSuspendedEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    @Transactional
    public void handle(MemberSuspendedEvent event) {
        try {
            memberProfileService.changeProfilePublishStatus(event.getMemberId(), false);
            memberProfileService.changeMemberActivityStatus(event.getMemberId(), getStatusFromEvent(event));
        } catch (Exception e) {
            log.error("Member(id: {})의 프로필 업데이트 중 예외가 발생했습니다.", event.getMemberId(), e);
        }
    }

    private ActivityStatus getStatusFromEvent(MemberSuspendedEvent event) {
        if (event.getStatus().equals("TEMPORARY")) {
            return ActivityStatus.SUSPENDED_TEMPORARILY;
        } else {
            return ActivityStatus.SUSPENDED_PERMANENTLY;
        }
    }
}
