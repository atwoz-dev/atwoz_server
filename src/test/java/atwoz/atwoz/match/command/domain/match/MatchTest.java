package atwoz.atwoz.match.command.domain.match;

import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.match.command.domain.match.exception.InvalidMatchStatusChangeException;
import atwoz.atwoz.match.command.domain.match.vo.Message;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

public class MatchTest {

    @Nested
    @DisplayName("매치 요청 테스트")
    class Request {
        @Test
        @DisplayName("요청자 아이디가 null인 경우 예외 반환")
        void throwsExceptionWhenRequesterIdIsNull() {
            // Given
            Long requesterId = null;
            Long responderId = 2L;
            Message requestMessage = Message.from("매칭을 요청합니다!");
            MatchType type = MatchType.MATCH;
            MatchContactType contactType = MatchContactType.PHONE_NUMBER;

            // When & Then
            Assertions.assertThatThrownBy(
                    () -> Match.request(requesterId, responderId, requestMessage, "testUser", type, contactType))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("응답자 아이디가 null인 경우 예외 반환")
        void throwsExceptionWhenResponderIdIsNull() {
            // Given
            Long requesterId = 1L;
            Long responderId = null;
            Message requestMessage = Message.from("매칭을 요청합니다!");
            MatchType type = MatchType.MATCH;
            MatchContactType contactType = MatchContactType.PHONE_NUMBER;

            // When & Then
            Assertions.assertThatThrownBy(
                    () -> Match.request(requesterId, responderId, requestMessage, "testUser", type, contactType))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("요청 메세지가 null인 경우 예외 반환")
        void throwsExceptionWhenMessageIsNull() {
            // Given
            Long requesterId = 1L;
            Long responderId = 2L;
            Message requestMessage = null;
            MatchType type = MatchType.MATCH;
            MatchContactType contactType = MatchContactType.PHONE_NUMBER;

            // When & Then
            Assertions.assertThatThrownBy(
                    () -> Match.request(requesterId, responderId, requestMessage, "testUser", type, contactType))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("모든 값이 null이 아닌 경우 성공.")
        void createMatch() {
            // Given
            Long requesterId = 1L;
            Long responderId = 2L;
            Message requestMessage = Message.from("매칭을 요청합니다.");
            MatchType type = MatchType.MATCH;
            MatchContactType contactType = MatchContactType.PHONE_NUMBER;

            try (MockedStatic<Events> eventsMockedStatic = mockStatic(Events.class)) {
                // When
                Match match = Match.request(requesterId, responderId, requestMessage, "testUser", type, contactType);

                // Then
                Assertions.assertThat(match.getRequesterId()).isEqualTo(requesterId);
                Assertions.assertThat(match.getResponderId()).isEqualTo(responderId);
                Assertions.assertThat(match.getRequestMessage()).isEqualTo(requestMessage);
                eventsMockedStatic.verify(() -> Events.raise(any()), times(2));
            }
        }
    }

    @Nested
    @DisplayName("매치 상태 변경 테스트")
    class ChangeStatus {

        @Test
        @DisplayName("현재 값이 Waiting 상태가 아닌 경우, 예외 반환")
        void throwsExceptionWhenStatusIsNotWaiting() {
            // Given
            Long requesterId = 1L;
            Long responderId = 2L;
            Message requestMessage = Message.from("매칭을 요청합니다.");
            Message responseMessage = Message.from("매칭을 수락합니다.");
            Match match;
            MatchType type = MatchType.MATCH;
            MatchContactType contactType = MatchContactType.PHONE_NUMBER;

            try (MockedStatic<Events> eventsMockedStatic = mockStatic(Events.class)) {
                match = Match.request(requesterId, responderId, requestMessage, "testUser", type, contactType);
                match.expire();
            }

            // When & Then
            Assertions.assertThatThrownBy(() -> match.approve(responseMessage, "testUser", contactType))
                .isInstanceOf(InvalidMatchStatusChangeException.class);
        }

        @Test
        @DisplayName("현재 값이 Waiting 상태인 경우, 예외 반환")
        void changeStatus() {
            // Given
            Long requesterId = 1L;
            Long responderId = 2L;
            Message requestMessage = Message.from("매칭을 요청합니다.");
            Message responseMessage = Message.from("매칭을 수락합니다.");
            MatchType type = MatchType.MATCH;
            MatchContactType contactType = MatchContactType.PHONE_NUMBER;

            Match match;
            try (MockedStatic<Events> eventsMockedStatic = mockStatic(Events.class)) {
                match = Match.request(requesterId, responderId, requestMessage, "testUser", type, contactType);

                // When
                match.approve(responseMessage, "testUser", contactType);
            }

            // Then
            Assertions.assertThat(match.getStatus()).isEqualTo(MatchStatus.MATCHED);
            Assertions.assertThat(match.getResponseMessage().getValue()).isEqualTo(responseMessage.getValue());
        }

        @Test
        @DisplayName("현재 값이 Reject 상태가 아닌 경우, Check 상태로 변경 시, 예외 반환")
        void throwsExceptionWhenStatusIsNotReject() {
            // Given
            Long requesterId = 1L;
            Long responderId = 2L;
            Message requestMessage = Message.from("매칭을 요청합니다.");
            MatchType type = MatchType.MATCH;
            Match match;
            MatchContactType contactType = MatchContactType.PHONE_NUMBER;

            try (MockedStatic<Events> eventsMockedStatic = mockStatic(Events.class)) {
                match = Match.request(requesterId, responderId, requestMessage, "testUser", type, contactType);
            }

            // When & Then
            Assertions.assertThatThrownBy(match::checkRejected)
                .isInstanceOf(InvalidMatchStatusChangeException.class);
        }

        @Test
        @DisplayName("현재 값이 Reject 상태인 경우, Check 상태로 변경.")
        void changeStatusToCheck() {
            // Given
            Long requesterId = 1L;
            Long responderId = 2L;
            Message requestMessage = Message.from("매칭을 요청합니다.");
            MatchType type = MatchType.MATCH;
            MatchContactType contactType = MatchContactType.PHONE_NUMBER;

            Match match;
            try (MockedStatic<Events> eventsMockedStatic = mockStatic(Events.class)) {
                match = Match.request(requesterId, responderId, requestMessage, "testUser", type, contactType);
                match.reject("testUser");
            }

            // When
            match.checkRejected();

            // Then
            Assertions.assertThat(match.getStatus()).isEqualTo(MatchStatus.REJECT_CHECKED);
        }
    }

    @Nested
    @DisplayName("매치 읽음 처리 테스트")
    class ReadMatch {
        @Test
        @DisplayName("응답자가 매치를 읽은 경우, 읽음 처리")
        void readByResponder() {
            // Given
            Long requesterId = 1L;
            Long responderId = 2L;
            Message requestMessage = Message.from("매칭을 요청합니다.");
            MatchType type = MatchType.MATCH;
            MatchContactType contactType = MatchContactType.PHONE_NUMBER;

            Match match;
            try (MockedStatic<Events> eventsMockedStatic = mockStatic(Events.class)) {
                match = Match.request(requesterId, responderId, requestMessage, "testUser", type, contactType);
            }

            // When
            match.read(responderId);

            // Then
            Assertions.assertThat(match.getReadByResponderAt()).isNotNull();
        }

        @Test
        @DisplayName("요청자가 매치를 읽은 경우, 읽음 처리 안함")
        void readByRequester() {
            // Given
            Long requesterId = 1L;
            Long responderId = 2L;
            Message requestMessage = Message.from("매칭을 요청합니다.");
            MatchType type = MatchType.MATCH;
            MatchContactType contactType = MatchContactType.PHONE_NUMBER;

            Match match;
            try (MockedStatic<Events> eventsMockedStatic = mockStatic(Events.class)) {
                match = Match.request(requesterId, responderId, requestMessage, "testUser", type, contactType);
            }

            // When
            match.read(requesterId);

            // Then
            Assertions.assertThat(match.getReadByResponderAt()).isNull();
        }

        @Test
        @DisplayName("이미 읽은 매치를 읽은 경우, 읽음 처리 안함")
        void readAlreadyReadMatch() {
            // Given
            Long requesterId = 1L;
            Long responderId = 2L;
            Message requestMessage = Message.from("매칭을 요청합니다.");
            MatchType type = MatchType.MATCH;
            MatchContactType contactType = MatchContactType.PHONE_NUMBER;

            Match match;
            try (MockedStatic<Events> eventsMockedStatic = mockStatic(Events.class)) {
                match = Match.request(requesterId, responderId, requestMessage, "testUser", type, contactType);
            }

            // When
            match.read(responderId);
            var firstReadAt = match.getReadByResponderAt();
            match.read(responderId);

            // Then
            Assertions.assertThat(match.getReadByResponderAt()).isEqualTo(firstReadAt);
        }
    }
}
