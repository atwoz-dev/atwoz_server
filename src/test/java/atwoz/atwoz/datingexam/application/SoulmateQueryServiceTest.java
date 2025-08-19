package atwoz.atwoz.datingexam.application;

import atwoz.atwoz.datingexam.application.required.DatingExamSubmitRepository;
import atwoz.atwoz.datingexam.application.required.SoulmateQueryRepository;
import atwoz.atwoz.datingexam.domain.DatingExamSubmit;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SoulmateQueryServiceTest {

    @InjectMocks
    private SoulmateQueryService soulmateQueryService;

    @Mock
    private DatingExamSubmitRepository datingExamSubmitRepository;

    @Mock
    private SoulmateQueryRepository soulmateQueryRepository;

    @Nested
    @DisplayName("소울 메이트 아이디 목록을 조회할 때,")
    class FindSoulmateIds {

        @Test
        @DisplayName("유저의 연애 모의고사 제출 기록이 없다면, EntityNotFoundException을 던진다.")
        void whenNoDatingExamSubmitThenThrowEntityNotFoundException() {
            // given
            Long memberId = 1L;
            when(datingExamSubmitRepository.findByMemberId(memberId))
                .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> soulmateQueryService.findSoulmateIds(memberId)
            ).isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("유저의 연애 모의고사 제출 기록이 있지만 필수 과목이 제출되지 않았다면, IllegalStateException을 던진다.")
        void whenDatingExamSubmitExistsButNotRequiredThenThrowIllegalStateException() {
            // given
            Long memberId = 1L;
            DatingExamSubmit datingExamSubmit = mock(DatingExamSubmit.class);
            when(datingExamSubmitRepository.findByMemberId(memberId))
                .thenReturn(Optional.of(datingExamSubmit));
            when(datingExamSubmit.isRequiredSubjectSubmitted()).thenReturn(false);

            // when & then
            assertThatThrownBy(() -> soulmateQueryService.findSoulmateIds(memberId)
            ).isInstanceOf(IllegalStateException.class);
        }
    }
}