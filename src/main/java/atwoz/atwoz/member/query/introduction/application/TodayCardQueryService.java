package atwoz.atwoz.member.query.introduction.application;

import atwoz.atwoz.member.query.introduction.intra.InterviewAnswerQueryResult;
import atwoz.atwoz.member.query.introduction.intra.IntroductionQueryRepository;
import atwoz.atwoz.member.query.introduction.intra.MemberIntroductionProfileQueryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TodayCardQueryService {
    private final TodayCardMemberIdFetcher todayCardMemberIdFetcher;
    private final IntroductionQueryRepository introductionQueryRepository;

    public List<MemberIntroductionProfileView> findTodayCardIntroductions(long memberId) {
        Set<Long> todayCardMemberIds = todayCardMemberIdFetcher.fetch(memberId, IntroductionCacheKeyPrefix.TODAY_CARD);
        List<MemberIntroductionProfileQueryResult> memberIntroductionProfileQueryResults =
            introductionQueryRepository.findAllMemberIntroductionProfileQueryResultByMemberIds(memberId,
                todayCardMemberIds);
        List<InterviewAnswerQueryResult> interviewAnswerQueryResults =
            introductionQueryRepository.findAllInterviewAnswerInfoByMemberIds(todayCardMemberIds);
        return MemberIntroductionProfileViewMapper.mapWithDefaultTag(memberIntroductionProfileQueryResults,
            interviewAnswerQueryResults);
    }
}
