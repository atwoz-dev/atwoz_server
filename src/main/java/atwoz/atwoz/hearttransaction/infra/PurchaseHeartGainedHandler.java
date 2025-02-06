package atwoz.atwoz.hearttransaction.infra;

import atwoz.atwoz.hearttransaction.application.HeartTransactionService;
import atwoz.atwoz.member.command.domain.member.event.PurchaseHeartGained;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class PurchaseHeartGainedHandler {
    private final HeartTransactionService heartTransactionService;

    @Async
    @TransactionalEventListener(value = PurchaseHeartGained.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PurchaseHeartGained event) {
        heartTransactionService.createHeartPurchaseTransaction(event.getMemberId(), event.getAmount(), event.getMissionHeartBalance(), event.getPurchaseHeartBalance());
    }
}
