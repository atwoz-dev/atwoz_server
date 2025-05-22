package atwoz.atwoz.member.query.introduction.application;

import atwoz.atwoz.member.query.introduction.intra.InterviewAnswerQueryResult;
import atwoz.atwoz.member.query.introduction.intra.MemberIntroductionProfileQueryResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberIntroductionProfileViewMapper {

    public static List<MemberIntroductionProfileView> mapWithDefaultTag(
        List<MemberIntroductionProfileQueryResult> profileResults,
        List<InterviewAnswerQueryResult> interviewResults
    ) {
        Map<Long, String> interviewAnswerMap = getFirstInterviewAnswerMap(interviewResults);
        return profileResults.stream()
            .map(result -> new MemberIntroductionProfileView(
                result.memberId(),
                result.profileImageUrl(),
                result.hobbies().stream().toList(),
                result.mbti(),
                result.religion(),
                interviewAnswerMap.get(result.memberId()),
                result.likeLevel(),
                result.isIntroduced()
            ))
            .toList();
    }

    public static List<MemberIntroductionProfileView> mapWithSameHobbyTag(
        List<MemberIntroductionProfileQueryResult> profileResults,
        List<InterviewAnswerQueryResult> interviewResults
    ) {
        Map<Long, String> interviewAnswerMap = getFirstInterviewAnswerMap(interviewResults);
        return profileResults.stream()
            .map(result -> new MemberIntroductionProfileView(
                result.memberId(),
                result.profileImageUrl(),
                List.of(),
                result.mbti(),
                result.religion(),
                interviewAnswerMap.get(result.memberId()),
                result.likeLevel(),
                result.isIntroduced()
            ))
            .toList();
    }

    public static List<MemberIntroductionProfileView> mapWithSameReligionTag(
        List<MemberIntroductionProfileQueryResult> profileResults,
        List<InterviewAnswerQueryResult> interviewResults
    ) {
        Map<Long, String> interviewAnswerMap = getFirstInterviewAnswerMap(interviewResults);
        return profileResults.stream()
            .map(result -> new MemberIntroductionProfileView(
                result.memberId(),
                result.profileImageUrl(),
                List.of(),
                result.mbti(),
                null,
                interviewAnswerMap.get(result.memberId()),
                result.likeLevel(),
                result.isIntroduced()
            ))
            .toList();
    }

    private static Map<Long, String> getFirstInterviewAnswerMap(List<InterviewAnswerQueryResult> interviewResults) {
        return interviewResults.stream()
            .collect(Collectors.toMap(
                InterviewAnswerQueryResult::memberId,
                InterviewAnswerQueryResult::content,
                (existing, replacement) -> existing
            ));
    }
}
