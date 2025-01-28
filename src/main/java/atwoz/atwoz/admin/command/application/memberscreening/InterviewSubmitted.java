package atwoz.atwoz.admin.command.application.memberscreening;

import atwoz.atwoz.common.event.Event;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// TODO: 인터뷰 쪽 개발되면 그쪽으로 옮겨야함
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class InterviewSubmitted extends Event {

    private final Long memberId;

    public static InterviewSubmitted from(Long memberId) {
        return new InterviewSubmitted(memberId);
    }
}
