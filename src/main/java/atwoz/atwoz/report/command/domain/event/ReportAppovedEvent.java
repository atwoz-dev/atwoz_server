package atwoz.atwoz.report.command.domain.event;

import atwoz.atwoz.common.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReportAppovedEvent extends Event {
    private final long reporteeId;

    public static ReportAppovedEvent from(long reporteeId) {
        return new ReportAppovedEvent(reporteeId);
    }
}
