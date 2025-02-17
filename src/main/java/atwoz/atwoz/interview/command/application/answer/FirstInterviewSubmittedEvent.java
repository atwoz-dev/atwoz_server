package atwoz.atwoz.interview.command.application.answer;

import atwoz.atwoz.common.event.Event;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class FirstInterviewSubmittedEvent extends Event {

    private final Long memberId;

    public static FirstInterviewSubmittedEvent from(Long memberId) {
        return new FirstInterviewSubmittedEvent(memberId);
    }
}
