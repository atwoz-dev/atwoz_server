package atwoz.atwoz.notification.command.domain.notification.message;

import atwoz.atwoz.notification.command.domain.notification.NotificationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageTemplateFactory 테스트")
class MessageTemplateFactoryTest {

    @Nested
    @DisplayName("getByNotificationType() 메서드 테스트")
    class GetByNotificationTypeTest {

        @Test
        @DisplayName("MATCH_REQUESTED 타입 요청 시 MatchRequestedMessageTemplate을 반환한다.")
        void getByNotificationType_Returns_MatchRequestedTemplate() {
            // given
            MessageTemplate matchRequestedTemplate = Mockito.mock(MatchRequestedMessageTemplate.class);
            when(matchRequestedTemplate.getNotificationType()).thenReturn(NotificationType.MATCH_REQUESTED);
            MessageTemplateFactory factory = new MessageTemplateFactory(List.of(matchRequestedTemplate));

            // when
            MessageTemplate result = factory.getByNotificationType(NotificationType.MATCH_REQUESTED);

            // then
            assertThat(result).isEqualTo(matchRequestedTemplate);
        }

        @Test
        @DisplayName("INAPPROPRIATE_CONTENT 타입 요청 시 InappropriateContentMessageTemplate을 반환한다.")
        void getByNotificationType_Returns_InappropriateContentTemplate() {
            // given
            MessageTemplate inappropriateContentTemplate = Mockito.mock(InappropriateContentMessageTemplate.class);
            when(inappropriateContentTemplate.getNotificationType()).thenReturn(NotificationType.INAPPROPRIATE_CONTENT);
            MessageTemplateFactory factory = new MessageTemplateFactory(List.of(inappropriateContentTemplate));

            // when
            MessageTemplate result = factory.getByNotificationType(NotificationType.INAPPROPRIATE_CONTENT);

            // then
            assertThat(result).isEqualTo(inappropriateContentTemplate);
        }

        @Test
        @DisplayName("등록되지 않은 NotificationType 요청 시 DefaultMessageTemplate을 반환한다.")
        void getByNotificationType_Returns_DefaultTemplate_When_Not_Found() {
            // given
            MessageTemplateFactory factory = new MessageTemplateFactory(List.of());

            // when
            MessageTemplate result = factory.getByNotificationType(NotificationType.MATCH_REQUESTED);

            // then
            assertThat(result).isInstanceOf(DefaultMessageTemplate.class);
        }
    }
}