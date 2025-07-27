package atwoz.atwoz.notification.command.application;

import atwoz.atwoz.notification.command.domain.NotificationPreference;
import atwoz.atwoz.notification.command.domain.NotificationPreferenceCommandRepository;
import atwoz.atwoz.notification.presentation.NotificationPreferenceSetRequest;
import atwoz.atwoz.notification.presentation.NotificationPreferenceToggleRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static atwoz.atwoz.notification.command.domain.NotificationType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationPreferenceServiceTest {

    @Mock
    NotificationPreferenceCommandRepository repository;

    @InjectMocks
    NotificationPreferenceService service;

    @Test
    @DisplayName("create(): 신규 설정 없으면 저장 호출")
    void createSavesNewPreference() {
        // given
        long memberId = 1L;
        when(repository.existsByMemberId(memberId)).thenReturn(false);

        // when
        service.create(memberId);

        // then
        verify(repository).save(argThat(pref ->
            pref.getMemberId() == memberId && pref.isEnabledGlobally()
        ));
    }

    @Test
    @DisplayName("create(): 이미 설정 있으면 예외 발생")
    void createThrowsWhenDuplicate() {
        // given
        long memberId = 2L;
        when(repository.existsByMemberId(memberId)).thenReturn(true);

        // when && then
        assertThatThrownBy(() -> service.create(memberId))
            .isInstanceOf(DuplicateNotificationPreferenceException.class);
    }

    @Test
    @DisplayName("enableGlobally(): 글로벌 활성화")
    void enableGloballySetsTrue() {
        // given
        long memberId = 3L;
        var pref = NotificationPreference.of(memberId);
        pref.disableGlobally();
        when(repository.findByMemberId(memberId)).thenReturn(Optional.of(pref));

        // when
        service.enableGlobally(memberId);

        // then
        assertThat(pref.isEnabledGlobally()).isTrue();
    }

    @Test
    @DisplayName("enableGlobally(): 설정 없으면 예외 발생")
    void enableGloballyThrowsWhenNotFound() {
        // given
        long memberId = 7L;
        when(repository.findByMemberId(memberId)).thenReturn(Optional.empty());

        // when && then
        assertThatThrownBy(() -> service.enableGlobally(memberId))
            .isInstanceOf(NotificationPreferenceNotFoundException.class);
    }

    @Test
    @DisplayName("disableGlobally(): 글로벌 비활성화")
    void disableGloballySetsFalse() {
        // given
        long memberId = 4L;
        var pref = NotificationPreference.of(memberId);
        when(repository.findByMemberId(memberId)).thenReturn(Optional.of(pref));

        // when
        service.disableGlobally(memberId);

        // then
        assertThat(pref.isEnabledGlobally()).isFalse();
    }

    @Test
    @DisplayName("enableForType(): 특정 타입 활성화")
    void enableForTypeSetsTrue() {
        // given
        long memberId = 5L;
        var pref = NotificationPreference.of(memberId);
        pref.disableForNotificationType(LIKE);
        when(repository.findByMemberId(memberId)).thenReturn(Optional.of(pref));
        var req = new NotificationPreferenceToggleRequest("LIKE");

        // when
        service.enableForType(memberId, req);

        // then
        assertThat(pref.canReceive(LIKE)).isTrue();
    }

    @Test
    @DisplayName("enableForType(): 설정 없으면 예외 발생")
    void enableForTypeThrowsWhenNotFound() {
        long memberId = 8L;
        when(repository.findByMemberId(memberId)).thenReturn(Optional.empty());
        var req = new NotificationPreferenceToggleRequest("MATCH_REQUEST");

        assertThatThrownBy(() -> service.enableForType(memberId, req))
            .isInstanceOf(NotificationPreferenceNotFoundException.class);
    }

    @Test
    @DisplayName("disableForType(): 특정 타입 비활성화")
    void disableForTypeSetsFalse() {
        // given
        long memberId = 6L;
        var pref = NotificationPreference.of(memberId);
        when(repository.findByMemberId(memberId)).thenReturn(Optional.of(pref));
        var req = new NotificationPreferenceToggleRequest("MATCH_REQUEST");

        // when
        service.disableForType(memberId, req);

        // then
        assertThat(pref.canReceive(MATCH_REQUEST)).isFalse();
    }

    @Test
    @DisplayName("disableForType(): 설정 없으면 예외 발생")
    void disableForTypeThrowsWhenNotFound() {
        long memberId = 9L;
        when(repository.findByMemberId(memberId)).thenReturn(Optional.empty());
        var req = new NotificationPreferenceToggleRequest("LIKE");

        assertThatThrownBy(() -> service.disableForType(memberId, req))
            .isInstanceOf(NotificationPreferenceNotFoundException.class);
    }

    @Test
    @DisplayName("setNotificationPreferences(): 전달된 문자열 키를 enum으로 매핑하여 업데이트")
    void setNotificationPreferencesUpdatesSpecifiedTypes() {
        long memberId = 10L;
        var pref = NotificationPreference.of(memberId);
        when(repository.findByMemberId(memberId)).thenReturn(Optional.of(pref));

        var updates = Map.of(MATCH_REQUEST.name(), false, LIKE.name(), false);
        var req = new NotificationPreferenceSetRequest(updates);

        service.setNotificationPreferences(memberId, req);

        assertThat(pref.canReceive(MATCH_REQUEST)).isFalse();
        assertThat(pref.canReceive(LIKE)).isFalse();
        assertThat(pref.canReceive(MATCH_ACCEPT)).isTrue();
    }

    @Test
    @DisplayName("setNotificationPreferences(): 설정 없으면 예외 발생")
    void setNotificationPreferencesThrowsWhenNotFound() {
        long memberId = 11L;
        when(repository.findByMemberId(memberId)).thenReturn(Optional.empty());
        var req = new NotificationPreferenceSetRequest(Map.of());

        assertThatThrownBy(() -> service.setNotificationPreferences(memberId, req))
            .isInstanceOf(NotificationPreferenceNotFoundException.class);
    }
}
