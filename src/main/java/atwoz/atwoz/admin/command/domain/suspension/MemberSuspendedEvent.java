package atwoz.atwoz.admin.command.domain.suspension;

import atwoz.atwoz.common.event.Event;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberSuspendedEvent extends Event {

    private final long memberId;
    private final String status;

    public static MemberSuspendedEvent of(long senderId, @NonNull String status) {
        return new MemberSuspendedEvent(senderId, status);
    }
}
