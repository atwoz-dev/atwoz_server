package atwoz.atwoz.admin.command.domain.suspension;

import atwoz.atwoz.common.event.Event;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberUnsuspendedEvent extends Event {

    private final long memberId;

    public static MemberUnsuspendedEvent of(long memberId) {
        return new MemberUnsuspendedEvent(memberId);
    }
}
