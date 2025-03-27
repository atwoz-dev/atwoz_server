package atwoz.atwoz.notification.command.domain.notification.message;

import atwoz.atwoz.notification.command.domain.notification.NotificationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MessageTemplateFactory 테스트")
class MessageTemplateFactoryTest {

    private final MessageTemplateFactory factory = new MessageTemplateFactory();

    @Test
    @DisplayName("MATCH_REQUESTED 타입은 MatchRequestedMessageTemplate를 반환한다.")
    void createMatchRequestedTemplate() {
        // given
        NotificationType type = NotificationType.MATCH_REQUESTED;
        String receiverName = "홍길동";
        MessageTemplateParameters params = MessageTemplateParameters.of(type, receiverName);

        // when
        MessageTemplate template = factory.create(params);

        // then
        assertThat(template).isInstanceOf(MatchRequestedMessageTemplate.class);
        assertThat(template.getTitle()).isEqualTo(receiverName + "님께 매치가 요청되었습니다.");
        assertThat(template.getContent()).isNull();
    }

    @Test
    @DisplayName("INAPPROPRIATE_CONTENT 타입은 InappropriateContentMessageTemplate를 반환한다.")
    void createInappropriateContentTemplate() {
        // given
        NotificationType type = NotificationType.INAPPROPRIATE_CONTENT;
        MessageTemplateParameters params = MessageTemplateParameters.from(type);

        // when
        MessageTemplate template = factory.create(params);

        // then
        assertThat(template).isInstanceOf(InappropriateContentMessageTemplate.class);
        assertThat(template.getTitle()).isEqualTo("작성하신 게시글에 부적절한 내용이 포함되어 있습니다. 다른 사용자들에게 불쾌감을 줄 수 있는 게시글은 삭제될 수 있습니다.");
        assertThat(template.getContent()).isNull();
    }
}
