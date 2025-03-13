package atwoz.atwoz.member.query.introduction.application;

import atwoz.atwoz.member.query.introduction.intra.InterviewAnswerQueryResult;
import atwoz.atwoz.member.query.introduction.intra.MemberIntroductionProfileQueryResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MemberIntroductionProfileViewMapper {

    public static List<MemberIntroductionProfileView> mapWithDefaultTag(
            List<MemberIntroductionProfileQueryResult> profileResults,
            List<InterviewAnswerQueryResult> interviewResults
    ) {
        return map(profileResults, interviewResults, MemberIntroductionProfileViewMapper::toDefaultTags);
    }

    public static List<MemberIntroductionProfileView> mapWithSameHobbyTag(
            List<MemberIntroductionProfileQueryResult> profileResults,
            List<InterviewAnswerQueryResult> interviewResults
    ) {
        return map(profileResults, interviewResults, MemberIntroductionProfileViewMapper::toSameHobbyTags);
    }

    public static List<MemberIntroductionProfileView> mapWithSameReligionTag(
            List<MemberIntroductionProfileQueryResult> profileResults,
            List<InterviewAnswerQueryResult> interviewResults
    ) {
        return map(profileResults, interviewResults, MemberIntroductionProfileViewMapper::toSameReligionTags);
    }

    private static List<MemberIntroductionProfileView> map(
            List<MemberIntroductionProfileQueryResult> profileResults,
            List<InterviewAnswerQueryResult> interviewResults,
            Function<MemberIntroductionProfileQueryResult, List<String>> tagExtractor
    ) {
        Map<Long, String> interviewAnswerMap = getFirstInterviewAnswerMap(interviewResults);
        return profileResults.stream()
                .map(result -> new MemberIntroductionProfileView(
                        result.memberId(),
                        result.profileImageUrl(),
                        tagExtractor.apply(result),
                        interviewAnswerMap.get(result.memberId()),
                        result.isIntroduced()
                ))
                .collect(Collectors.toList());
    }

    private static Map<Long, String> getFirstInterviewAnswerMap(List<InterviewAnswerQueryResult> interviewResults) {
        return interviewResults.stream()
                .collect(Collectors.toMap(
                        InterviewAnswerQueryResult::memberId,
                        InterviewAnswerQueryResult::content,
                        (existing, replacement) -> existing
                ));
    }

    private static List<String> toDefaultTags(MemberIntroductionProfileQueryResult view) {
        List<String> tags = new ArrayList<>();
        tags.addAll(view.hobbies());
        tags.add(view.religion());
        tags.add(view.mbti());
        return tags;
    }

    private static List<String> toSameHobbyTags(MemberIntroductionProfileQueryResult view) {
        List<String> tags = new ArrayList<>();
        tags.add(view.religion());
        tags.add(view.mbti());
        return tags;
    }

    private static List<String> toSameReligionTags(MemberIntroductionProfileQueryResult view) {
        List<String> tags = new ArrayList<>();
        tags.addAll(view.hobbies());
        tags.add(view.mbti());
        return tags;
    }
}
