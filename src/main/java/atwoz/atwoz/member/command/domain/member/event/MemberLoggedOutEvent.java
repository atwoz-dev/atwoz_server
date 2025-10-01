package atwoz.atwoz.member.command.domain.member.event;

import atwoz.atwoz.common.event.Event;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberLoggedOutEvent extends Event {

    private final long memberId;

    public static MemberLoggedOutEvent from(long memberId) {
        return new MemberLoggedOutEvent(memberId);
    }
}