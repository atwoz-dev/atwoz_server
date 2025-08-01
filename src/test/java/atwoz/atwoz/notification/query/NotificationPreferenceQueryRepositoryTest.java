package atwoz.atwoz.notification.query;

import atwoz.atwoz.QuerydslConfig;
import atwoz.atwoz.notification.command.domain.NotificationPreference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static atwoz.atwoz.notification.command.domain.NotificationType.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QuerydslConfig.class, NotificationPreferenceQueryRepository.class})
class NotificationPreferenceQueryRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private NotificationPreferenceQueryRepository repository;

    @Test
    @DisplayName("findByMemberId(): 존재하는 회원의 알림 설정 조회")
    void findByMemberIdExistingMember() {
        // given
        var preference = NotificationPreference.of(1L);
        preference.disableForNotificationType(LIKE);
        preference.enableForNotificationType(MATCH_REQUEST);
        em.persist(preference);
        em.flush();
        em.clear();

        // when
        Optional<NotificationPreferenceView> result = repository.findByMemberId(1L);

        // then
        assertThat(result).isPresent();

        NotificationPreferenceView view = result.get();
        assertThat(view.memberId()).isEqualTo(1L);
        assertThat(view.isEnabledGlobally()).isTrue();
        assertThat(view.preferences()).containsEntry("LIKE", false);
        assertThat(view.preferences()).containsEntry("MATCH_REQUEST", true);
    }

    @Test
    @DisplayName("findByMemberId(): 존재하지 않는 회원의 알림 설정 조회")
    void findByMemberIdNonExistingMember() {
        // given
        var preference = NotificationPreference.of(1L);
        em.persist(preference);
        em.flush();
        em.clear();

        // when
        Optional<NotificationPreferenceView> result = repository.findByMemberId(999L);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByMemberId(): 전역 알림이 비활성화된 경우")
    void findByMemberIdGloballyDisabled() {
        // given
        var preference = NotificationPreference.of(2L);
        preference.disableGlobally();
        preference.enableForNotificationType(LIKE);
        em.persist(preference);
        em.flush();
        em.clear();

        // when
        Optional<NotificationPreferenceView> result = repository.findByMemberId(2L);

        // then
        assertThat(result).isPresent();

        NotificationPreferenceView view = result.get();
        assertThat(view.memberId()).isEqualTo(2L);
        assertThat(view.isEnabledGlobally()).isFalse();
        assertThat(view.preferences()).containsEntry("LIKE", true);
    }

    @Test
    @DisplayName("findByMemberId(): 기본 설정 확인 (모든 알림 활성화)")
    void findByMemberIdDefaultSettings() {
        // given
        var preference = NotificationPreference.of(3L);
        em.persist(preference);
        em.flush();
        em.clear();

        // when
        Optional<NotificationPreferenceView> result = repository.findByMemberId(3L);

        // then
        assertThat(result).isPresent();

        NotificationPreferenceView view = result.get();
        assertThat(view.memberId()).isEqualTo(3L);
        assertThat(view.isEnabledGlobally()).isTrue();

        for (var notificationType : values()) {
            assertThat(view.preferences())
                .containsEntry(notificationType.name(), true);
        }
    }

    @Test
    @DisplayName("findByMemberId(): 일부 알림 타입만 비활성화된 경우")
    void findByMemberIdPartiallyDisabled() {
        // given
        var preference = NotificationPreference.of(4L);
        preference.disableForNotificationType(LIKE);
        preference.enableForNotificationType(MATCH_REQUEST);
        preference.enableForNotificationType(PROFILE_EXCHANGE_REQUEST);
        em.persist(preference);
        em.flush();
        em.clear();

        // when
        Optional<NotificationPreferenceView> result = repository.findByMemberId(4L);

        // then
        assertThat(result).isPresent();

        NotificationPreferenceView view = result.get();
        assertThat(view.memberId()).isEqualTo(4L);
        assertThat(view.isEnabledGlobally()).isTrue();

        assertThat(view.preferences()).containsEntry("LIKE", false);

        assertThat(view.preferences()).containsEntry("MATCH_REQUEST", true);
        assertThat(view.preferences()).containsEntry("PROFILE_EXCHANGE_REQUEST", true);
    }
}