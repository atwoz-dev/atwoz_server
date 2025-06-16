package atwoz.atwoz.member.command.application.member;

import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class MemberDeleteSchedulerTest {
    @Mock
    private MemberCommandRepository memberCommandRepository;

    @InjectMocks
    private MemberDeleteScheduler memberDeleteScheduler;

    @Test
    @DisplayName("삭제 대상이 존재하는 경우, 해당 대상들을 삭제한다.")
    void deleteWhenTargetExists() {
        // Given
        List<Long> memberIds = List.of(1L, 2L, 3L);

        Mockito.when(memberCommandRepository.findIdDeletedBefore(Mockito.any()))
            .thenReturn(memberIds);

        // When
        memberDeleteScheduler.delete();

        // Then
        Mockito.verify(memberCommandRepository, Mockito.times(1))
            .deleteInIds(memberIds);
    }
}
