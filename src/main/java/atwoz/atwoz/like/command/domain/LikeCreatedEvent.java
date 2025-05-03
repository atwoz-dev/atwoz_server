package atwoz.atwoz.like.command.domain;

import atwoz.atwoz.common.event.Event;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeCreatedEvent extends Event {
    private final long senderId;
    private final long receiverId;

    public static LikeCreatedEvent of(long senderId, long receiverId) {
        return new LikeCreatedEvent(senderId, receiverId);
    }
}
