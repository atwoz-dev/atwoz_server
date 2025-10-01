package atwoz.atwoz.member.command.domain.member.event;

import atwoz.atwoz.common.event.Event;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberLoggedInEvent extends Event {

    private final long memberId;

    public static MemberLoggedInEvent from(long memberId) {
        return new MemberLoggedInEvent(memberId);
    }
}