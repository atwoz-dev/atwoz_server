package atwoz.atwoz.notification.command.domain.notification;

import atwoz.atwoz.notification.command.domain.notification.message.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static atwoz.atwoz.notification.command.domain.notification.NotificationType.INAPPROPRIATE_CONTENT;
import static atwoz.atwoz.notification.command.domain.notification.NotificationType.MATCH_REQUESTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
        @DisplayName("MessageTemplateFactory와 MessageGenerator를 통해 제목과 내용을 설정한다.")
        void setMessageUpdatesTitleAndContent() {
            // given
            Notification notification = createNotification(MATCH_REQUESTED);

            MessageTemplateFactory factory = Mockito.mock(MessageTemplateFactory.class);
            MessageGenerator generator = Mockito.mock(MessageGenerator.class);

            String receiverName = "홍길동";
            MessageTemplate template = MatchRequestedMessageTemplate.from(receiverName);

            when(factory.create(any(MessageTemplateParameters.class))).thenReturn(template);
            when(generator.createTitle(template)).thenReturn(receiverName + "님께 매치가 요청되었습니다.");
            when(generator.createContent(template)).thenReturn(null);

            // when
            notification.setMessage(factory, generator, receiverName);

            // then
            assertThat(notification.getTitle()).isEqualTo(receiverName + "님께 매치가 요청되었습니다.");
            assertThat(notification.getContent()).isNull();
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
