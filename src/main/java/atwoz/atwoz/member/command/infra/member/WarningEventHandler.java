package atwoz.atwoz.member.command.infra.member;

import atwoz.atwoz.admin.command.domain.warning.WarningIssuedEvent;
import atwoz.atwoz.member.command.application.member.MemberProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@Slf4j
@RequiredArgsConstructor
public class WarningEventHandler {

    private final MemberProfileService memberProfileService;

    @TransactionalEventListener(value = WarningIssuedEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handle(WarningIssuedEvent event) {
        try {
            memberProfileService.changeProfilePublishStatus(event.getMemberId(), false);
        } catch (Exception e) {
            log.error("Member(id: {})의 프로필 업데이트 중 예외가 발생했습니다.", event.getMemberId(), e);
        }
    }
}
