package atwoz.atwoz.notification.command.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;

import static atwoz.atwoz.notification.command.domain.NotificationType.*;
import static org.assertj.core.api.Assertions.assertThat;

class NotificationPreferenceTest {

    @Test
    @DisplayName("of() 초기화: 글로벌과 타입별 수신 모두 활성화")
    void ofInitializesDefaults() {
        // given
        long memberId = 42L;

        // when
        var preference = NotificationPreference.of(memberId);

        // then
        assertThat(preference.getMemberId()).isEqualTo(memberId);
        assertThat(preference.isEnabledGlobally()).isTrue();
        assertThat(preference.canReceive(MATCH_REQUEST)).isTrue();
        assertThat(preference.canReceive(LIKE)).isTrue();
    }

    @Test
    @DisplayName("disableGlobally(): 글로벌 비활성화 시 모든 타입 수신 불가")
    void disableGloballyDisablesAllTypes() {
        // given
        var preference = NotificationPreference.of(1L);

        // when
        preference.disableGlobally();

        // then
        assertThat(preference.isEnabledGlobally()).isFalse();
        assertThat(preference.canReceive(MATCH_REQUEST)).isFalse();
        assertThat(preference.canReceive(LIKE)).isFalse();
    }

    @Test
    @DisplayName("enableGlobally(): 글로벌 활성화 시 원복 확인")
    void enableGloballyRestoresAllTypes() {
        // given
        var preference = NotificationPreference.of(1L);
        preference.disableGlobally();

        // when
        preference.enableGlobally();

        // then
        assertThat(preference.isEnabledGlobally()).isTrue();
        assertThat(preference.canReceive(MATCH_REQUEST)).isTrue();
        assertThat(preference.canReceive(LIKE)).isTrue();
    }

    @Test
    @DisplayName("disableForNotificationType(): 특정 타입만 비활성화")
    void disableForNotificationTypeAffectsOnlyThatType() {
        // given
        var preference = NotificationPreference.of(1L);

        // when
        preference.disableForNotificationType(MATCH_REQUEST);

        // then
        assertThat(preference.canReceive(MATCH_REQUEST)).isFalse();
        assertThat(preference.canReceive(LIKE)).isTrue();
    }

    @Test
    @DisplayName("enableForNotificationType(): 특정 타입만 활성화")
    void enableForNotificationTypeRestoresOnlyThatType() {
        // given
        var preference = NotificationPreference.of(1L);
        preference.disableForNotificationType(MATCH_REQUEST);

        // when
        preference.enableForNotificationType(MATCH_REQUEST);

        // then
        assertThat(preference.canReceive(MATCH_REQUEST)).isTrue();
    }

    @Test
    @DisplayName("isDisabledForType(): 내부 맵 값 그대로 반환")
    void isDisabledForTypeReflectsInternalMap() {
        // given
        var preference = NotificationPreference.of(1L);
        boolean before = preference.isDisabledForType(MATCH_REQUEST);

        // when
        preference.disableForNotificationType(MATCH_REQUEST);
        boolean after = preference.isDisabledForType(MATCH_REQUEST);

        // then
        assertThat(before).isFalse();
        assertThat(after).isTrue();
    }

    @Test
    @DisplayName("getNotificationPreferences(): 반환된 맵은 내부 상태의 복사본이어야 함")
    void getNotificationPreferencesReturnsCopy() {
        // given
        var preference = NotificationPreference.of(1L);

        // when
        Map<NotificationType, Boolean> returnedPreferences = preference.getNotificationPreferences();
        returnedPreferences.put(MATCH_REQUEST, false);

        // then
        assertThat(returnedPreferences.get(MATCH_REQUEST)).isFalse();
        assertThat(preference.canReceive(MATCH_REQUEST)).isTrue();
    }

    @Test
    @DisplayName("updateNotificationPreferences(): 지정된 타입만 업데이트")
    void updateNotificationPreferencesUpdatesSpecifiedTypes() {
        // given
        var preference = NotificationPreference.of(1L);
        Map<NotificationType, Boolean> updates = new EnumMap<>(NotificationType.class);
        updates.put(MATCH_REQUEST, false);
        updates.put(LIKE, false);

        // when
        preference.updateNotificationPreferences(updates);

        // then
        assertThat(preference.canReceive(MATCH_REQUEST)).isFalse();
        assertThat(preference.canReceive(LIKE)).isFalse();
        assertThat(preference.canReceive(MATCH_ACCEPT)).isTrue();
    }
}
