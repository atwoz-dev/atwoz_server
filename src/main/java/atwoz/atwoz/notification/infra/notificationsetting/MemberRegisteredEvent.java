package atwoz.atwoz.notification.infra.notificationsetting;

import atwoz.atwoz.common.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// TODO: 패키지 위치 이동 및 회원가입시 event raise 필요
@RequiredArgsConstructor
@Getter
public class MemberRegisteredEvent extends Event {

    private final long memberId;

    public static MemberRegisteredEvent from(long memberId) {
        return new MemberRegisteredEvent(memberId);
    }
}