package atwoz.atwoz.admin.command.domain.warning;

import atwoz.atwoz.common.event.Event;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class WarningIssuedEvent extends Event {

    private final long adminId;
    private final long memberId;
    private final String reasonType;

    private WarningIssuedEvent(long adminId, long memberId, @NonNull String reasonType) {
        this.adminId = adminId;
        this.memberId = memberId;
        this.reasonType = reasonType;
    }

    public static WarningIssuedEvent of(long adminId, long memberId, String reasonType) {
        return new WarningIssuedEvent(adminId, memberId, reasonType);
    }
}
