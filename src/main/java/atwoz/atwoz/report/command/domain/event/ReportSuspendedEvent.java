package atwoz.atwoz.report.command.domain.event;

import atwoz.atwoz.common.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReportSuspendedEvent extends Event {
    private final long reporteeId;

    public static ReportSuspendedEvent from(long reporteeId) {
        return new ReportSuspendedEvent(reporteeId);
    }
}
