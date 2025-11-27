package atwoz.atwoz.admin.infra.suspension;

import atwoz.atwoz.admin.command.application.suspension.SuspensionService;
import atwoz.atwoz.admin.command.domain.suspension.SuspensionStatus;
import atwoz.atwoz.admin.presentation.suspension.SuspendRequest;
import atwoz.atwoz.report.command.domain.event.ReportSuspendedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class ReportSuspendedEventHandler {

    private final SuspensionService suspensionService;

    @Async
    @TransactionalEventListener(value = ReportSuspendedEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ReportSuspendedEvent event) {
        SuspendRequest request = new SuspendRequest(SuspensionStatus.TEMPORARY.name());
        suspensionService.suspendByAdmin(event.getAdminId(), event.getReporteeId(), request);
    }
}