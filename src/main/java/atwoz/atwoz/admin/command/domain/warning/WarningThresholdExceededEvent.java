package atwoz.atwoz.admin.command.domain.warning;

import atwoz.atwoz.common.event.Event;
import lombok.Getter;

@Getter
public class WarningThresholdExceededEvent extends Event {

    private final long adminId;
    private final long memberId;

    private WarningThresholdExceededEvent(long adminId, long memberId) {
        this.adminId = adminId;
        this.memberId = memberId;
    }

    public static WarningThresholdExceededEvent of(long adminId, long memberId) {
        return new WarningThresholdExceededEvent(adminId, memberId);
    }
}
