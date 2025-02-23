package atwoz.atwoz.match.command.application.match;

import atwoz.atwoz.match.command.application.match.exception.ExistsMatchException;
import atwoz.atwoz.match.command.domain.match.MatchRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private MatchService matchService;

    @Test
    @DisplayName("이미 두 유저간의 진행중인 매치가 존재하는 경우, 예외 발생")
    void throwsExceptionWhenExistsMatch() {
        // Given
        Long requesterId = 1L;
        Long responderId = 2L;
        String requestMessage = "매칭을 요청합니다!";

        Mockito.doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(1);
            runnable.run();
            return null;
        }).when(matchRepository).withNamedLock(Mockito.any(), Mockito.any());

        Mockito.when(matchRepository.existsActiveMatchBetween(requesterId, responderId))
                .thenReturn(true);

        // When & Then
        Assertions.assertThatThrownBy(() -> matchService.request(requesterId, responderId, requestMessage))
                .isInstanceOf(ExistsMatchException.class);
    }


    @Test
    @DisplayName("두 유저 사이의 매치가 존재하지 않는 경우 매치 생성.")
    void createMatch() {
        // Given
        Long requesterId = 1L;
        Long responderId = 2L;
        String requestMessage = "매칭을 요청합니다!";

        Mockito.doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(1);
            runnable.run();
            return null;
        }).when(matchRepository).withNamedLock(Mockito.any(), Mockito.any());

        Mockito.when(matchRepository.existsActiveMatchBetween(requesterId, responderId))
                .thenReturn(false);

        // When
        matchService.request(requesterId, responderId, requestMessage);

        // Then
        Mockito.verify(matchRepository).save(
                Mockito.argThat(match -> match.getRequesterId().equals(requesterId) &&
                        match.getResponderId().equals(responderId) &&
                        match.getRequestMessage().getValue().equals(requestMessage))
        );
    }
}
