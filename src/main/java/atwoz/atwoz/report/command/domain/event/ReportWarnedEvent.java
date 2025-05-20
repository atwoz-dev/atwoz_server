package atwoz.atwoz.report.command.domain.event;

import atwoz.atwoz.common.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReportWarnedEvent extends Event {
    private final long reporteeId;
    private final String reportReason;

    public static ReportWarnedEvent of(long reporteeId, String reportReason) {
        return new ReportWarnedEvent(reporteeId, reportReason);
    }
}
