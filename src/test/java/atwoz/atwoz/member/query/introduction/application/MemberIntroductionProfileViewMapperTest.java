package atwoz.atwoz.member.query.introduction.application;

import atwoz.atwoz.member.command.domain.member.Hobby;
import atwoz.atwoz.member.query.introduction.intra.InterviewAnswerQueryResult;
import atwoz.atwoz.member.query.introduction.intra.MemberIntroductionProfileQueryResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class MemberIntroductionProfileViewMapperTest {
    private final Long expectedMemberId = 1L;
    private final String expectedProfileImageUrl = "imageUrl";
    private final Set<String> expectedHobbies = Set.of(Hobby.CAMPING.name(), Hobby.WINE.name());
    private final String expectedReligion = "Buddhist";
    private final String expectedMbti = "INFJ";
    private final String expectedLikeLevel = "INTEREST";
    private final String expectedInterviewAnswer = "Third Answer";
    private final boolean expectedIsIntroduced = false;

    @Test
    @DisplayName("mapWithDefaultTag 메서드 테스트")
    void mapWithDefaultTagTest() {
        // given
        List<String> expectedTags = new ArrayList<>(List.of());
        expectedTags.addAll(expectedHobbies);
        expectedTags.add(expectedReligion);
        expectedTags.add(expectedMbti);

        List<MemberIntroductionProfileQueryResult> profileResults = getProfileQueryResults();
        List<InterviewAnswerQueryResult> interviewResults = getInterviewResults();

        // when
        List<MemberIntroductionProfileView> views =
                MemberIntroductionProfileViewMapper.mapWithDefaultTag(profileResults, interviewResults);

        // then
        assertProfileView(views, expectedTags);
    }

    @Test
    @DisplayName("mapWithSameHobbyTag 메서드 테스트")
    void mapWithSameHobbyTagTest() {
        // given
        List<String> expectedTags = new ArrayList<>(List.of());
        expectedTags.add(expectedReligion);
        expectedTags.add(expectedMbti);

        List<MemberIntroductionProfileQueryResult> profileResults = getProfileQueryResults();
        List<InterviewAnswerQueryResult> interviewResults = getInterviewResults();

        // when
        List<MemberIntroductionProfileView> views =
                MemberIntroductionProfileViewMapper.mapWithSameHobbyTag(profileResults, interviewResults);

        // then
        assertProfileView(views, expectedTags);
    }

    @Test
    @DisplayName("mapWithSameReligionTag 메서드 테스트")
    void mapWithSameReligionTagTest() {
        // given
        List<String> expectedTags = new ArrayList<>(List.of());
        expectedTags.addAll(expectedHobbies);
        expectedTags.add(expectedMbti);

        List<MemberIntroductionProfileQueryResult> profileResults = getProfileQueryResults();
        List<InterviewAnswerQueryResult> interviewResults = getInterviewResults();

        // when
        List<MemberIntroductionProfileView> views =
                MemberIntroductionProfileViewMapper.mapWithSameReligionTag(profileResults, interviewResults);

        // then
        assertProfileView(views, expectedTags);
    }

    private List<MemberIntroductionProfileQueryResult> getProfileQueryResults() {
        MemberIntroductionProfileQueryResult profileResult = new MemberIntroductionProfileQueryResult(
                expectedMemberId,
                expectedProfileImageUrl,
                expectedHobbies,
                expectedReligion,
                expectedMbti,
                expectedLikeLevel,
                expectedIsIntroduced
        );

        return List.of(profileResult);
    }

    private List<InterviewAnswerQueryResult> getInterviewResults() {
        InterviewAnswerQueryResult interviewResult = new InterviewAnswerQueryResult(
                expectedMemberId,
                expectedInterviewAnswer
        );

        return List.of(interviewResult);
    }

    private void assertProfileView(List<MemberIntroductionProfileView> views, List<String> expectedTags) {
        assertThat(views).hasSize(1);
        MemberIntroductionProfileView view = views.get(0);
        assertThat(view.memberId()).isEqualTo(expectedMemberId);
        assertThat(view.profileImageUrl()).isEqualTo(expectedProfileImageUrl);
        List<String> tags = view.tags();
        assertThat(tags).containsExactlyElementsOf(expectedTags);
        assertThat(view.interviewAnswerContent()).isEqualTo(expectedInterviewAnswer);
        assertThat(view.likeLevel()).isEqualTo(expectedLikeLevel);
        assertThat(view.isIntroduced()).isEqualTo(expectedIsIntroduced);
    }
}