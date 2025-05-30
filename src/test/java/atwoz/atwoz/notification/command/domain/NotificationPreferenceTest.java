package atwoz.atwoz.notification.command.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static atwoz.atwoz.notification.command.domain.NotificationType.LIKE;
import static atwoz.atwoz.notification.command.domain.NotificationType.MATCH_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;

class NotificationPreferenceTest {

    @Test
    @DisplayName("of() 초기화: 글로벌과 타입별 수신 모두 활성화")
    void ofInitializesDefaults() {
        // given
        long memberId = 42L;

        // when
        var pref = NotificationPreference.of(memberId);

        // then
        assertThat(pref.getMemberId()).isEqualTo(memberId);
        assertThat(pref.isEnabledGlobally()).isTrue();
        assertThat(pref.canReceive(MATCH_REQUEST)).isTrue();
        assertThat(pref.canReceive(LIKE)).isTrue();
    }

    @Test
    @DisplayName("disableGlobally(): 글로벌 비활성화 시 모든 타입 수신 불가")
    void disableGloballyDisablesAllTypes() {
        // given
        var pref = NotificationPreference.of(1L);

        // when
        pref.disableGlobally();

        // then
        assertThat(pref.isEnabledGlobally()).isFalse();
        assertThat(pref.canReceive(MATCH_REQUEST)).isFalse();
        assertThat(pref.canReceive(LIKE)).isFalse();
    }

    @Test
    @DisplayName("enableGlobally(): 글로벌 활성화 시 원복 확인")
    void enableGloballyRestoresAllTypes() {
        // given
        var pref = NotificationPreference.of(1L);
        pref.disableGlobally();

        // when
        pref.enableGlobally();

        // then
        assertThat(pref.isEnabledGlobally()).isTrue();
        assertThat(pref.canReceive(MATCH_REQUEST)).isTrue();
        assertThat(pref.canReceive(LIKE)).isTrue();
    }

    @Test
    @DisplayName("disableForNotificationType(): 특정 타입만 비활성화")
    void disableForNotificationTypeAffectsOnlyThatType() {
        // given
        var pref = NotificationPreference.of(1L);

        // when
        pref.disableForNotificationType(MATCH_REQUEST);

        // then
        assertThat(pref.canReceive(MATCH_REQUEST)).isFalse();
        assertThat(pref.canReceive(LIKE)).isTrue();
    }

    @Test
    @DisplayName("enableForNotificationType(): 특정 타입만 활성화")
    void enableForNotificationTypeRestoresOnlyThatType() {
        // given
        var pref = NotificationPreference.of(1L);
        pref.disableForNotificationType(MATCH_REQUEST);

        // when
        pref.enableForNotificationType(MATCH_REQUEST);

        // then
        assertThat(pref.canReceive(MATCH_REQUEST)).isTrue();
    }

    @Test
    @DisplayName("isDisabledForType(): 내부 맵 값 그대로 반환")
    void isDisabledForTypeReflectsInternalMap() {
        // given
        var pref = NotificationPreference.of(1L);
        boolean before = pref.isDisabledForType(MATCH_REQUEST);

        // when
        pref.disableForNotificationType(MATCH_REQUEST);
        boolean after = pref.isDisabledForType(MATCH_REQUEST);

        // then
        assertThat(before).isFalse();
        assertThat(after).isTrue();
    }
}
