package atwoz.atwoz.admin.command.domain.warning;

import atwoz.atwoz.common.event.Event;
import lombok.Getter;

@Getter
public class WarningThresholdExceededEvent extends Event {
    private final long memberId;

    private WarningThresholdExceededEvent(long memberId) {
        this.memberId = memberId;
    }

    public static WarningThresholdExceededEvent of(long memberId) {
        return new WarningThresholdExceededEvent(memberId);
    }
}
