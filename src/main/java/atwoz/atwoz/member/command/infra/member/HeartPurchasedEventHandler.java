package atwoz.atwoz.member.command.infra.member;

import atwoz.atwoz.member.command.application.member.MemberHeartBalanceService;
import atwoz.atwoz.payment.domain.event.HeartPurchasedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class HeartPurchasedEventHandler {
    private final MemberHeartBalanceService memberHeartBalanceService;

    @Async
    @TransactionalEventListener(value = HeartPurchasedEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handle(HeartPurchasedEvent event) {
        try {
            memberHeartBalanceService.grantPurchasedHearts(event.getMemberId(), event.getAmount());
        } catch (Exception e) {
            // TODO: 하트 지급 실패시 보상 트랜잭션으로 로그를 남기고, 관리자에게 알림을 보낸다.
        }
    }
}
