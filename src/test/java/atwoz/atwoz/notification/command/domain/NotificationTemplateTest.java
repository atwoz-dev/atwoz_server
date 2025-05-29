package atwoz.atwoz.notification.command.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static atwoz.atwoz.notification.command.domain.NotificationType.LIKE;
import static atwoz.atwoz.notification.command.domain.NotificationType.MATCH_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;

class NotificationTemplateTest {

    @Test
    @DisplayName("of(): 타입과 템플릿, 활성화 상태가 올바르게 초기화됨")
    void ofInitializesFields() {
        // given
        var type = MATCH_REQUEST;
        var titleTpl = "Hello {user}";
        var bodyTpl = "You have {count} new requests";

        // when
        var tpl = NotificationTemplate.of(type, titleTpl, bodyTpl);

        // then
        assertThat(tpl.getType()).isEqualTo(type);
        assertThat(tpl.getTitleTemplate()).isEqualTo(titleTpl);
        assertThat(tpl.getBodyTemplate()).isEqualTo(bodyTpl);
        assertThat(tpl.isActive()).isTrue();
    }

    @Test
    @DisplayName("generateTitle(): 템플릿 파라미터가 전달된 값으로 치환됨")
    void generateTitleReplacesParams() {
        // given
        var tpl = NotificationTemplate.of(LIKE, "좋아요: {sender}님이 보냄", "");
        var params = Map.of("sender", "Alice");

        // when
        var title = tpl.generateTitle(params);

        // then
        assertThat(title).isEqualTo("좋아요: Alice님이 보냄");
    }

    @Test
    @DisplayName("generateBody(): 누락된 파라미터는 '{error}'로 대체됨")
    void generateBodyMissingParam() {
        // given
        var tpl = NotificationTemplate.of(LIKE, "", "이벤트: {event}, 사용자: {user}");
        var params = Map.of("event", "LIKE");

        // when
        var body = tpl.generateBody(params);

        // then
        assertThat(body).isEqualTo("이벤트: LIKE, 사용자: {error}");
    }

    @Test
    @DisplayName("generateTitle(): 플레이스홀더가 없으면 원본 템플릿 반환")
    void generateTitleWithoutPlaceholders() {
        // given
        var tpl = NotificationTemplate.of(LIKE, "고정 텍스트", "");

        // when
        var title = tpl.generateTitle(Map.of());

        // then
        assertThat(title).isEqualTo("고정 텍스트");
    }

    @Test
    @DisplayName("updateTitleTemplate(), updateBodyTemplate(): 템플릿 내용이 변경됨")
    void updateTemplates() {
        // given
        var tpl = NotificationTemplate.of(MATCH_REQUEST, "old {x}", "old {y}");

        // when
        tpl.updateTitleTemplate("new {a}");
        tpl.updateBodyTemplate("new {b}");

        // then
        assertThat(tpl.getTitleTemplate()).isEqualTo("new {a}");
        assertThat(tpl.getBodyTemplate()).isEqualTo("new {b}");
    }

    @Test
    @DisplayName("activate(), deactivate(): 활성화 상태가 토글됨")
    void activateDeactivate() {
        // given
        var tpl = NotificationTemplate.of(LIKE, "", "");
        tpl.deactivate();
        assertThat(tpl.isActive()).isFalse();

        // when
        tpl.activate();

        // then
        assertThat(tpl.isActive()).isTrue();
    }
}
