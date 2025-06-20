package atwoz.atwoz.member.command.domain.member.event;

import atwoz.atwoz.common.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MemberRegisteredEvent extends Event {

    private final long memberId;

    public static MemberRegisteredEvent from(long memberId) {
        return new MemberRegisteredEvent(memberId);
    }
}