package atwoz.atwoz.notification.command.domain.notification;

import atwoz.atwoz.notification.command.domain.notification.message.DefaultMessageTemplate;
import atwoz.atwoz.notification.command.domain.notification.message.MessageTemplate;
import atwoz.atwoz.notification.command.domain.notification.message.MessageTemplateParameters;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static atwoz.atwoz.notification.command.domain.notification.NotificationType.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Notification 테스트")
class NotificationTest {

    private Notification createNotification(NotificationType type) {
        return Notification.of(1L, SenderType.MEMBER, 2L, type);
    }

    @Nested
    @DisplayName("isSocialType() 메서드 테스트")
    class IsSocialTypeTest {

        @Test
        @DisplayName("Social 유형을 가진 경우 true를 반환한다.")
        void isSocialTypeReturnsTrue() {
            // given
            Notification notification = createNotification(MATCH_REQUESTED);

            // when
            boolean result = notification.isSocialType();

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Social 유형이 아닌 경우 false를 반환한다.")
        void isSocialTypeReturnsFalse() {
            // given
            Notification notification = createNotification(INAPPROPRIATE_CONTENT);

            // when
            boolean result = notification.isSocialType();

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("setMessage() 메서드 테스트")
    class SetMessageTest {

        @Test
        @DisplayName("MessageTemplate과 MessageTemplateParameters를 통해 제목과 내용을 설정한다.")
        void setMessageUpdatesTitleAndContent() {
            // given
            Notification notification = createNotification(NONE);

            MessageTemplate template = new DefaultMessageTemplate();
            MessageTemplateParameters parameters = MessageTemplateParameters.of(
                    notification.getSenderId(),
                    notification.getReceiverId()
            );

            // when
            notification.setMessage(template, parameters);

            // then
            assertThat(notification.getTitle()).isEqualTo("제목");
            assertThat(notification.getContent()).isEqualTo("내용");
        }
    }

    @Nested
    @DisplayName("markAsRead() 메서드 테스트")
    class MarkAsReadTest {

        @Test
        @DisplayName("markAsRead()를 호출하면 isRead가 true로 변경된다.")
        void markAsReadSetsIsReadToTrue() {
            // given
            Notification notification = createNotification(MATCH_REQUESTED);

            // when
            notification.markAsRead();

            // then
            assertThat(notification.getIsRead()).isTrue();
        }
    }
}
