package atwoz.atwoz.member.query.introduction.application;

import atwoz.atwoz.member.query.introduction.intra.InterviewAnswerQueryResult;
import atwoz.atwoz.member.query.introduction.intra.IntroductionQueryRepository;
import atwoz.atwoz.member.query.introduction.intra.MemberIntroductionProfileQueryResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodayCardQueryServiceTest {

    @InjectMocks
    private TodayCardQueryService todayCardQueryService;

    @Mock
    private TodayCardMemberIdFetcher todayCardMemberIdFetcher;

    @Mock
    private IntroductionQueryRepository introductionQueryRepository;

    @Test
    @DisplayName("findTodayCardIntroductions 메서드 테스트")
    void findTodayCardIntroductions() {
        // Given
        long memberId = 1L;
        Set<Long> todayCardMemberIds = Set.of(2L, 3L, 4L);
        when(todayCardMemberIdFetcher.fetch(memberId, IntroductionCacheKeyPrefix.TODAY_CARD))
            .thenReturn(todayCardMemberIds);

        List<MemberIntroductionProfileQueryResult> memberIntroductionProfileQueryResults = List.of(
            mock(MemberIntroductionProfileQueryResult.class));
        when(introductionQueryRepository.findAllMemberIntroductionProfileQueryResultByMemberIds(memberId,
            todayCardMemberIds))
            .thenReturn(memberIntroductionProfileQueryResults);

        final List<InterviewAnswerQueryResult> interviewAnswerQueryResults = List.of(
            mock(InterviewAnswerQueryResult.class));
        when(introductionQueryRepository.findAllInterviewAnswerInfoByMemberIds(todayCardMemberIds))
            .thenReturn(interviewAnswerQueryResults);

        try (MockedStatic<MemberIntroductionProfileViewMapper> mockedMapper = mockStatic(
            MemberIntroductionProfileViewMapper.class)) {
            List<MemberIntroductionProfileView> views = List.of(mock(MemberIntroductionProfileView.class));
            mockedMapper.when(() -> MemberIntroductionProfileViewMapper.mapWithDefaultTag(
                    memberIntroductionProfileQueryResults, interviewAnswerQueryResults))
                .thenReturn(views);

            // When
            List<MemberIntroductionProfileView> result = todayCardQueryService.findTodayCardIntroductions(memberId);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result).containsExactlyElementsOf(views);
        }
    }
}