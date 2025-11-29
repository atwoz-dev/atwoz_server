package deepple.deepple.member.command.domain.member.event;

import deepple.deepple.common.event.Event;
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