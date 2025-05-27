package atwoz.atwoz.notification.command.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static atwoz.atwoz.notification.command.domain.NotificationStatus.*;
import static atwoz.atwoz.notification.command.domain.NotificationType.LIKE;
import static atwoz.atwoz.notification.command.domain.SenderType.MEMBER;
import static atwoz.atwoz.notification.command.domain.SenderType.SYSTEM;
import static org.assertj.core.api.Assertions.assertThat;

class NotificationTest {

    @Test
    @DisplayName("정상 데이터로 생성 시 필드가 모두 설정되고 초기 상태(readAt/status)는 null")
    void createWithValidArguments() {
        // given
        var senderType = MEMBER;
        long senderId = 100L;
        long receiverId = 200L;
        var type = LIKE;
        String title = "테스트 제목";
        String body = "테스트 본문";

        // when
        var notification = Notification.create(senderType, senderId, receiverId, type, title, body);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getSenderType()).isEqualTo(senderType);
        assertThat(notification.getSenderId()).isEqualTo(senderId);
        assertThat(notification.getReceiverId()).isEqualTo(receiverId);
        assertThat(notification.getType()).isEqualTo(type);
        assertThat(notification.getTitle()).isEqualTo(title);
        assertThat(notification.getBody()).isEqualTo(body);
        assertThat(notification.getReadAt()).isNull();
        assertThat(notification.getStatus()).isNull();
        assertThat(notification.isRead()).isFalse();
    }

    @Test
    @DisplayName("markAsRead 호출 후 isRead가 true로 변경되고 readAt이 설정됨")
    void markAsReadChangesState() {
        // given
        var notification = Notification.create(SYSTEM, 1L, 2L, LIKE, "t", "b");

        // when
        notification.markAsRead();

        // then
        assertThat(notification.isRead()).isTrue();
        assertThat(notification.getReadAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("markAsRead를 여러 번 호출해도 readAt은 최초 한 번만 설정됨")
    void markAsReadIdempotent() {
        // given
        var notification = Notification.create(SYSTEM, 1L, 2L, LIKE, "t", "b");

        // when
        notification.markAsRead();
        LocalDateTime firstTime = notification.getReadAt();
        notification.markAsRead();

        // then
        assertThat(notification.getReadAt()).isEqualTo(firstTime);
    }

    @Test
    @DisplayName("markAsSent 호출 시 status가 SENT로 변경됨")
    void markAsSentSetsStatus() {
        // given
        var notification = Notification.create(MEMBER, 1L, 2L, LIKE, "t", "b");

        // when
        notification.markAsSent();

        // then
        assertThat(notification.getStatus()).isEqualTo(SENT);
    }

    @Test
    @DisplayName("markAsFailedDueToUnsupportedChannel 호출 시 status가 FAILED_UNSUPPORTED_CHANNEL로 변경됨")
    void markAsFailedUnsupportedChannelSetsStatus() {
        // given
        var notification = Notification.create(MEMBER, 1L, 2L, LIKE, "t", "b");

        // when
        notification.markAsFailedDueToUnsupportedChannel();

        // then
        assertThat(notification.getStatus()).isEqualTo(FAILED_UNSUPPORTED_CHANNEL);
    }

    @Test
    @DisplayName("markAsFailedDueToException 호출 시 status가 FAILED_EXCEPTION로 변경됨")
    void markAsFailedExceptionSetsStatus() {
        // given
        var notification = Notification.create(MEMBER, 1L, 2L, LIKE, "t", "b");

        // when
        notification.markAsFailedDueToException();

        // then
        assertThat(notification.getStatus()).isEqualTo(FAILED_EXCEPTION);
    }

    @Test
    @DisplayName("markAsRejectedByPreference 호출 시 status가 REJECTED로 변경됨")
    void markAsRejectedByPreferenceSetsStatus() {
        // given
        var notification = Notification.create(MEMBER, 1L, 2L, LIKE, "t", "b");

        // when
        notification.markAsRejectedByPreference();

        // then
        assertThat(notification.getStatus()).isEqualTo(REJECTED);
    }
}
