package atwoz.atwoz.like.command.domain.like.event;

import atwoz.atwoz.common.event.Event;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeMakedEvent extends Event {
    private final Long senderId;
    private final Long receiverId;

    public static LikeMakedEvent of(Long senderId, Long receiverId) {
        return new LikeMakedEvent(senderId, receiverId);
    }
}
