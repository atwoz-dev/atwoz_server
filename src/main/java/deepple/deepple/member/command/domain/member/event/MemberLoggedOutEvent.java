package deepple.deepple.member.command.domain.member.event;

import deepple.deepple.common.event.Event;
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