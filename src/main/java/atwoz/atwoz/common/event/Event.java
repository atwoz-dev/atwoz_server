package atwoz.atwoz.common.event;

import lombok.Getter;

import java.time.Instant;

@Getter
public abstract class Event {

    private final Instant timestamp;

    protected Event() {
        timestamp = Instant.now();
    }
}
