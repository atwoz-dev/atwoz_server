package atwoz.atwoz.admin.infra.warning;

import atwoz.atwoz.admin.command.application.warning.WarningService;
import atwoz.atwoz.admin.presentation.warning.WarningCreateRequest;
import atwoz.atwoz.report.command.domain.event.ReportWarnedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReportWarnedEventHandler {

    private static final Map<String, String> REASON_TYPE_MAP = Map.of(
        "STOLEN_IMAGE", "STOLEN_IMAGE",
        "INAPPROPRIATE_IMAGE", "INAPPROPRIATE_PROFILE_IMAGE",
        "EXPLICIT_CONTENT", "EXPLICIT_CONTENT",
        "OFFENSIVE_LANGUAGE", "OFFENSIVE_LANGUAGE",
        "CONTACT_IN_PROFILE", "CONTACT_IN_PROFILE",
        "ETC", "ETC"
    );

    private final WarningService warningService;

    @Async
    @TransactionalEventListener(value = ReportWarnedEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ReportWarnedEvent event) {
        String warningReasonType = REASON_TYPE_MAP.getOrDefault(event.getReportReason(), "ETC");
        WarningCreateRequest request = new WarningCreateRequest(
            event.getReporteeId(),
            Set.of(warningReasonType),
            true
        );
        warningService.issue(event.getAdminId(), request);
    }
}