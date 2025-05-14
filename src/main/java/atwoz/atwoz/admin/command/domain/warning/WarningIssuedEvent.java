package atwoz.atwoz.admin.command.domain.warning;

import atwoz.atwoz.common.event.Event;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class WarningIssuedEvent extends Event {
    private final long adminId;
    private final long memberId;
    private final String reason;

    private WarningIssuedEvent(long adminId, long memberId, @NonNull String reason) {
        this.adminId = adminId;
        this.memberId = memberId;
        this.reason = reason;
    }

    public static WarningIssuedEvent of(long adminId, long memberId, String reason) {
        return new WarningIssuedEvent(adminId, memberId, reason);
    }
}
