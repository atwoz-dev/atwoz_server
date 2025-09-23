package atwoz.atwoz.member.command.domain.member.event;

import atwoz.atwoz.common.event.Event;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberActivatedEvent extends Event {

    private final long memberId;

    public static MemberActivatedEvent from(long memberId) {
        return new MemberActivatedEvent(memberId);
    }
}