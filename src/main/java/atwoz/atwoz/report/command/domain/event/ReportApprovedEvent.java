package atwoz.atwoz.report.command.domain.event;

import atwoz.atwoz.common.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReportApprovedEvent extends Event {
    private final long reporteeId;

    public static ReportApprovedEvent from(long reporteeId) {
        return new ReportApprovedEvent(reporteeId);
    }
}
