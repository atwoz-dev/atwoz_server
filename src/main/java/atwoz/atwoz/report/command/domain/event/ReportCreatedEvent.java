package atwoz.atwoz.report.command.domain.event;

import atwoz.atwoz.common.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReportCreatedEvent extends Event {
    private final long reporterId;
    private final long reporteeId;

    public static ReportCreatedEvent of(long reporterId, long reporteeId) {
        return new ReportCreatedEvent(reporterId, reporteeId);
    }
}
