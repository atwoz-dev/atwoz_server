package deepple.deepple.report.command.domain.event;

import deepple.deepple.common.event.Event;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportCreatedEvent extends Event {
    private final long reporterId;
    private final long reporteeId;

    public static ReportCreatedEvent of(long reporterId, long reporteeId) {
        return new ReportCreatedEvent(reporterId, reporteeId);
    }
}
