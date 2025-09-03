package atwoz.atwoz.heart.command.infra.hearttransaction;

import atwoz.atwoz.heart.command.application.hearttransaction.HeartTransactionService;
import atwoz.atwoz.member.command.domain.member.event.MissionHeartGainedEvent;
import atwoz.atwoz.member.command.domain.member.event.PurchaseHeartGainedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class HeartTransactionEventHandler {
    private final HeartTransactionService heartTransactionService;

    @Async
    @TransactionalEventListener(value = PurchaseHeartGainedEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PurchaseHeartGainedEvent event) {
        heartTransactionService.createHeartPurchaseTransaction(event.getMemberId(), event.getAmount(),
            event.getMissionHeartBalance(), event.getPurchaseHeartBalance());
    }

    @Async
    @TransactionalEventListener(value = MissionHeartGainedEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handle(MissionHeartGainedEvent event) {
        heartTransactionService.createHeartMissionTransaction(event.getMemberId(), event.getAmount(),
            event.getMissionHeartBalance(), event.getPurchaseHeartBalance(), event.getActionType());
    }
}
