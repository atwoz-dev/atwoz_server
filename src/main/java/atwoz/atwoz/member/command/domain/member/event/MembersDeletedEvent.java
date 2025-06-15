package atwoz.atwoz.member.command.domain.member.event;

import atwoz.atwoz.common.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class MembersDeletedEvent extends Event {
    private final List<Long> memberIds;

    public static MembersDeletedEvent from(List<Long> memberIds) {
        return new MembersDeletedEvent(memberIds);
    }
}
