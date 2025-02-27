package atwoz.atwoz.match.command.domain.match;

import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.common.repository.LockRepository;
import atwoz.atwoz.match.command.domain.match.vo.Message;
import atwoz.atwoz.match.command.infra.match.MatchRepositoryImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.mockito.Mockito.mockStatic;

@Import({MatchRepositoryImpl.class, LockRepository.class})
@DataJpaTest
public class MatchRepositoryTest {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("서로 매치가 존재하는 경우 True 반환.")
    void getTrueWhenMatchExistsBetween() {
        // Given
        Long responderId = 1L;
        Long requesterId = 2L;
        String requestMessage = "매치를 신청합니다.";

        Match match;
        try (MockedStatic<Events> eventsMockedStatic = mockStatic(Events.class)) {
            match = Match.request(requesterId, responderId, Message.from(requestMessage));
        }
        entityManager.persist(match);
        entityManager.flush();

        // When
        boolean result = matchRepository.existsActiveMatchBetween(requesterId, responderId);
        boolean otherResult = matchRepository.existsActiveMatchBetween(responderId, requesterId);

        // Then
        Assertions.assertThat(result).isTrue();
        Assertions.assertThat(otherResult).isTrue();
    }

    @Test
    @DisplayName("매치가 존재하지 않는 경우 False 반환")
    void getFalseWhenMatchNotExistsBetween() {
        // Given
        Long requesterId = 1L;
        Long responderId = 2L;

        // When
        boolean result = matchRepository.existsActiveMatchBetween(requesterId, responderId);
        boolean otherResult = matchRepository.existsActiveMatchBetween(responderId, requesterId);

        // Then
        Assertions.assertThat(result).isFalse();
        Assertions.assertThat(otherResult).isFalse();
    }

    @Test
    @DisplayName("만료된 매치가 존재하는 경우 False 반환")
    void getFalseWhenExpiredMatchExistsBetween() {
        // Given
        Long requesterId = 1L;
        Long responderId = 2L;

        String requestMessage = "매치를 신청합니다.";

        Match match;
        try (MockedStatic<Events> eventsMockedStatic = mockStatic(Events.class)) {
            match = Match.request(requesterId, responderId, Message.from(requestMessage));
        }

        match.expire();
        entityManager.persist(match);
        entityManager.flush();

        // When
        boolean result = matchRepository.existsActiveMatchBetween(requesterId, responderId);
        boolean otherResult = matchRepository.existsActiveMatchBetween(responderId, requesterId);

        // Then
        Assertions.assertThat(result).isFalse();
        Assertions.assertThat(otherResult).isFalse();
    }
}
