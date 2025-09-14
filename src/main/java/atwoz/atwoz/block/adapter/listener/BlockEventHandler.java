package atwoz.atwoz.block.adapter.listener;

import atwoz.atwoz.block.application.provided.BlockCommander;
import atwoz.atwoz.report.command.domain.event.ReportCreatedEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class BlockEventHandler {
    private final BlockCommander blockCommander;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ReportCreatedEvent event) {
        Long blockerId = event.getReporterId();
        Long blockedId = event.getReporteeId();
        try {
            blockCommander.createBlock(blockerId, blockedId);
        } catch (Exception exception) {
            log.warn("신고 이벤트로 인한 차단 처리 중 오류가 발생했습니다. blockerId: {}, blockedId: {}", blockerId, blockedId, exception);
        }
    }
}
