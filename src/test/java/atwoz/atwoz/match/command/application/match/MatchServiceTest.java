package atwoz.atwoz.match.command.application.match;

import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.match.command.application.match.exception.ExistsMatchException;
import atwoz.atwoz.match.command.application.match.exception.MatchNotFoundException;
import atwoz.atwoz.match.command.domain.match.Match;
import atwoz.atwoz.match.command.domain.match.MatchRepository;
import atwoz.atwoz.match.command.domain.match.MatchStatus;
import atwoz.atwoz.match.command.domain.match.vo.Message;
import atwoz.atwoz.match.presentation.dto.MatchRequestDto;
import atwoz.atwoz.match.presentation.dto.MatchResponseDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;


@ExtendWith(MockitoExtension.class)
public class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private MatchService matchService;

    @Nested
    @DisplayName("매치 요청 테스트")
    class Request {
        @Test
        @DisplayName("이미 두 유저간의 진행중인 매치가 존재하는 경우, 예외 발생")
        void throwsExceptionWhenExistsMatch() {
            // Given
            Long requesterId = 1L;
            Long responderId = 2L;
            String requestMessage = "매칭을 요청합니다!";
            MatchRequestDto requestDto = new MatchRequestDto(responderId, requestMessage);

            Mockito.doAnswer(invocation -> {
                Runnable runnable = invocation.getArgument(1);
                runnable.run();
                return null;
            }).when(matchRepository).withNamedLock(any(), any());

            Mockito.when(matchRepository.existsActiveMatchBetween(requesterId, requestDto.responderId()))
                    .thenReturn(true);

            // When & Then
            Assertions.assertThatThrownBy(() -> matchService.request(requesterId, requestDto))
                    .isInstanceOf(ExistsMatchException.class);
        }


        @Test
        @DisplayName("두 유저 사이의 매치가 존재하지 않는 경우 매치 생성.")
        void createMatch() {
            // Given
            Long requesterId = 1L;
            Long responderId = 2L;
            String requestMessage = "매칭을 요청합니다!";
            MatchRequestDto requestDto = new MatchRequestDto(responderId, requestMessage);

            Mockito.doAnswer(invocation -> {
                Runnable runnable = invocation.getArgument(1);
                runnable.run();
                return null;
            }).when(matchRepository).withNamedLock(any(), any());

            Mockito.when(matchRepository.existsActiveMatchBetween(requesterId, requestDto.responderId()))
                    .thenReturn(false);

            // When
            try (MockedStatic<Events> eventsMockedStatic = mockStatic(Events.class)) {
                matchService.request(requesterId, requestDto);
            }

            // Then
            Mockito.verify(matchRepository).save(
                    Mockito.argThat(match -> match.getRequesterId().equals(requesterId) &&
                            match.getResponderId().equals(responderId) &&
                            match.getRequestMessage().getValue().equals(requestMessage))
            );
        }
    }

    @Nested
    @DisplayName("매치 수락 테스트")
    class Approve {

        @DisplayName("응답자의 아이디가 일치하지 않은 경우, 예외 발생")
        @Test
        void throwsExceptionWhenResponderIdIsNotEqual() {
            // Given
            Long responderId = 1L;
            Long matchId = 1L;
            String responseMessage = "매치 수락할게요";
            MatchResponseDto responseDto = new MatchResponseDto(responseMessage);

            Mockito.when(matchRepository.findByIdAndResponderId(matchId, responderId))
                    .thenReturn(Optional.empty());

            // When & Then
            Assertions.assertThatThrownBy(() -> matchService.approve(matchId, responderId, responseDto))
                    .isInstanceOf(MatchNotFoundException.class);
        }

        @DisplayName("매치의 상태가 대기중이 아닐 경우, 예외 발생")
        @Test
        void throwsExceptionWhenStatusIsNotWaiting() {
            // Given
            Long requesterId = 1L;
            Long responderId = 2L;
            Long matchId = 3L;
            String responseMessage = "매치 수락할게요";
            MatchResponseDto responseDto = new MatchResponseDto(responseMessage);
            Match match;

            try (MockedStatic<Events> eventsMockedStatic = mockStatic(Events.class)) {
                match = Match.request(requesterId, responderId, Message.from(responseMessage));
                match.reject(Message.from(responseMessage));
            }
            Mockito.when(matchRepository.findByIdAndResponderId(matchId, responderId))
                    .thenReturn(Optional.of(match));

            // When & Then
            Assertions.assertThatThrownBy(() -> matchService.approve(matchId, responderId, responseDto))
                    .isInstanceOf(MatchNotFoundException.class);
        }

        @DisplayName("매치의 상태가 대기중이며, 응답자 아이디가 일치할 경우 수락.")
        @Test
        void approveMatch() {
            // Given
            Long requesterId = 1L;
            Long responderId = 2L;
            Long matchId = 3L;
            String responseMessage = "매치 수락할게요";
            Match match;
            try (MockedStatic<Events> eventsMockedStatic = mockStatic(Events.class)) {
                match = Match.request(requesterId, responderId, Message.from(responseMessage));
            }

            MatchResponseDto responseDto = new MatchResponseDto(responseMessage);

            Mockito.when(matchRepository.findByIdAndResponderId(matchId, responderId))
                    .thenReturn(Optional.of(match));

            // When
            try (MockedStatic<Events> eventsMockedStatic = mockStatic(Events.class)) {
                matchService.approve(matchId, responderId, responseDto);
            }

            // Then
            Assertions.assertThat(match.getStatus()).isEqualTo(MatchStatus.MATCHED);
            Assertions.assertThat(match.getResponseMessage().getValue()).isEqualTo(responseMessage);
        }
    }

    @Nested
    @DisplayName("매치 거절 테스트")
    class Reject {

        @DisplayName("응답자의 아이디가 일치하지 않은 경우, 예외 발생")
        @Test
        void throwsExceptionWhenResponderIdIsNotEqual() {
            // Given
            Long responderId = 1L;
            Long matchId = 1L;
            String responseMessage = "매치 거절할게요";
            MatchResponseDto responseDto = new MatchResponseDto(responseMessage);

            Mockito.when(matchRepository.findByIdAndResponderId(matchId, responderId))
                    .thenReturn(Optional.empty());

            // When & Then
            Assertions.assertThatThrownBy(() -> matchService.reject(matchId, responderId, responseDto))
                    .isInstanceOf(MatchNotFoundException.class);
        }

        @DisplayName("매치의 상태가 대기중이 아닐 경우, 예외 발생")
        @Test
        void throwsExceptionWhenStatusIsNotWaiting() {
            // Given
            Long requesterId = 1L;
            Long responderId = 2L;
            Long matchId = 3L;
            String responseMessage = "매치 수락할게요";
            MatchResponseDto responseDto = new MatchResponseDto(responseMessage);

            Match match;
            try (MockedStatic<Events> eventsMockedStatic = mockStatic(Events.class)) {
                match = Match.request(requesterId, responderId, Message.from(responseMessage));
                match.approve(Message.from(responseMessage));
            }

            Mockito.when(matchRepository.findByIdAndResponderId(matchId, responderId))
                    .thenReturn(Optional.of(match));

            // When & Then
            Assertions.assertThatThrownBy(() -> matchService.reject(matchId, responderId, responseDto))
                    .isInstanceOf(MatchNotFoundException.class);
        }

        @DisplayName("매치의 상태가 대기중이며, 응답자 아이디가 일치할 경우 거절.")
        @Test
        void rejectMatch() {
            // Given
            Long requesterId = 1L;
            Long responderId = 2L;
            Long matchId = 3L;
            String requestMessage = "매치 신청할게요";
            String responseMessage = "매치 거절할게요";
            Match match;

            try (MockedStatic<Events> eventsMockedStatic = mockStatic(Events.class)) {
                match = Match.request(requesterId, responderId, Message.from(requestMessage));
            }

            MatchResponseDto responseDto = new MatchResponseDto(responseMessage);

            Mockito.when(matchRepository.findByIdAndResponderId(matchId, responderId))
                    .thenReturn(Optional.of(match));

            // When
            try (MockedStatic<Events> eventsMockedStatic = mockStatic(Events.class)) {
                matchService.reject(matchId, responderId, responseDto);
            }

            // Then
            Assertions.assertThat(match.getStatus()).isEqualTo(MatchStatus.REJECTED);
            Assertions.assertThat(match.getResponseMessage().getValue()).isEqualTo(responseMessage);
        }
    }

    @Nested
    @DisplayName("매치 거절 확인 테스트")
    class RejectCheck {

        @DisplayName("매치 상태가 거절이 아닌 경우, 예외 발생")
        @Test
        void throwsExceptionWhenMatchStatusIsNotRejected() {
            // Given
            Long requesterId = 1L;
            Long responderId = 2L;
            Long matchId = 3L;
            String requestMessage = "매치 신청할게요";

            Match match;
            try (MockedStatic<Events> eventsMockedStatic = mockStatic(Events.class)) {
                match = Match.request(requesterId, responderId, Message.from(requestMessage));
            }

            Mockito.when(matchRepository.findByIdAndRequesterId(matchId, requesterId))
                    .thenReturn(Optional.of(match));

            // When & Then
            Assertions.assertThatThrownBy(() -> matchService.rejectCheck(requesterId, matchId))
                    .isInstanceOf(MatchNotFoundException.class);
        }

        @DisplayName("매치가 존재하지 않는 경우, 예외 발생")
        @Test
        void throwsExceptionWhenRequesterIdIsNotEqualMemberId() {
            // Given
            Long requesterId = 2L;
            Long matchId = 3L;

            Mockito.when(matchRepository.findByIdAndRequesterId(matchId, requesterId))
                    .thenReturn(Optional.empty());

            // When & Then
            Assertions.assertThatThrownBy(() -> matchService.rejectCheck(requesterId, matchId))
                    .isInstanceOf(MatchNotFoundException.class);
        }

        @DisplayName("매치가 존재하며, 해당 상태가 거절일 경우 상태 변경")
        @Test
        void checkMatch() {
            // Given
            Long requesterId = 1L;
            Long responderId = 2L;
            Long matchId = 3L;
            String requestMessage = "매치 신청할게요";
            String responseMessage = "매치 거절할게요";

            Match match;
            try (MockedStatic<Events> eventsMockedStatic = mockStatic(Events.class)) {
                match = Match.request(requesterId, responderId, Message.from(requestMessage));
                match.reject(Message.from(responseMessage));
            }

            Mockito.when(matchRepository.findByIdAndRequesterId(matchId, requesterId))
                    .thenReturn(Optional.of(match));

            // When
            matchService.rejectCheck(requesterId, matchId);

            // Then
            Assertions.assertThat(match.getStatus()).isEqualTo(MatchStatus.REJECT_CHECKED);
        }
    }
}
