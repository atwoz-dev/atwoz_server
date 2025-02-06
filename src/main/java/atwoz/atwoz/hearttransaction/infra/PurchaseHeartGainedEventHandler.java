package atwoz.atwoz.hearttransaction.infra;

import atwoz.atwoz.hearttransaction.application.HeartTransactionService;
import atwoz.atwoz.member.command.domain.member.event.PurchaseHeartGainedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class PurchaseHeartGainedEventHandler {
    private final HeartTransactionService heartTransactionService;

    @Async
    @TransactionalEventListener(value = PurchaseHeartGainedEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PurchaseHeartGainedEvent event) {
        heartTransactionService.createHeartPurchaseTransaction(event.getMemberId(), event.getAmount(), event.getMissionHeartBalance(), event.getPurchaseHeartBalance());
    }
}
