package atwoz.atwoz.report.command.domain.event;

import atwoz.atwoz.common.event.Event;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportWarnedEvent extends Event {
    private final long adminId;
    private final long reporteeId;
    private final String reportReason;

    public static ReportWarnedEvent of(long adminId, long reporteeId, String reportReason) {
        return new ReportWarnedEvent(adminId, reporteeId, reportReason);
    }
}
