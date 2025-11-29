package deepple.deepple.member.query.introduction.application;

import deepple.deepple.member.command.domain.member.Hobby;
import deepple.deepple.member.query.introduction.intra.InterviewAnswerQueryResult;
import deepple.deepple.member.query.introduction.intra.MemberIntroductionProfileQueryResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class MemberIntroductionProfileViewMapperTest {
    private final Long memberId = 1L;
    private final String profileImageUrl = "imageUrl";
    private final Set<String> hobbies = Set.of(Hobby.CAMPING.name(), Hobby.WINE.name());
    private final String religion = "Buddhist";
    private final String mbti = "INFJ";
    private final String likeLevel = "INTEREST";
    private final String interviewAnswer = "Third Answer";
    private final boolean isIntroduced = false;

    @Test
    @DisplayName("mapWithDefaultTag 메서드 테스트")
    void mapWithDefaultTagTest() {
        // given
        List<String> expectedHobbies = hobbies.stream().toList();
        String expectedReligion = religion;
        String expectedMbti = mbti;

        List<MemberIntroductionProfileQueryResult> profileResults = getProfileQueryResults();
        List<InterviewAnswerQueryResult> interviewResults = getInterviewResults();

        // when
        List<MemberIntroductionProfileView> views =
            MemberIntroductionProfileViewMapper.mapWithDefaultTag(profileResults, interviewResults);

        // then
        assertProfileView(views, expectedHobbies, expectedReligion, expectedMbti);
    }

    @Test
    @DisplayName("mapWithSameHobbyTag 메서드 테스트")
    void mapWithSameHobbyTagTest() {
        // given
        List<String> expectedHobbies = List.of();
        String expectedReligion = religion;
        String expectedMbti = mbti;

        List<MemberIntroductionProfileQueryResult> profileResults = getProfileQueryResults();
        List<InterviewAnswerQueryResult> interviewResults = getInterviewResults();

        // when
        List<MemberIntroductionProfileView> views =
            MemberIntroductionProfileViewMapper.mapWithSameHobbyTag(profileResults, interviewResults);

        // then
        assertProfileView(views, expectedHobbies, expectedReligion, expectedMbti);
    }

    @Test
    @DisplayName("mapWithSameReligionTag 메서드 테스트")
    void mapWithSameReligionTagTest() {
        // given
        List<String> expectedHobbies = hobbies.stream().toList();
        String expectedReligion = null;
        String expectedMbti = mbti;

        List<MemberIntroductionProfileQueryResult> profileResults = getProfileQueryResults();
        List<InterviewAnswerQueryResult> interviewResults = getInterviewResults();

        // when
        List<MemberIntroductionProfileView> views =
            MemberIntroductionProfileViewMapper.mapWithSameReligionTag(profileResults, interviewResults);

        // then
        assertProfileView(views, expectedHobbies, expectedReligion, expectedMbti);

    }

    private List<MemberIntroductionProfileQueryResult> getProfileQueryResults() {
        MemberIntroductionProfileQueryResult profileResult = new MemberIntroductionProfileQueryResult(
            memberId,
            profileImageUrl,
            hobbies,
            religion,
            mbti,
            likeLevel,
            isIntroduced
        );

        return List.of(profileResult);
    }

    private List<InterviewAnswerQueryResult> getInterviewResults() {
        InterviewAnswerQueryResult interviewResult = new InterviewAnswerQueryResult(
            memberId,
            interviewAnswer
        );

        return List.of(interviewResult);
    }

    private void assertProfileView(List<MemberIntroductionProfileView> views, List<String> expectedHobbies,
        String expectedReligion, String expectedMbti) {
        assertThat(views).hasSize(1);
        MemberIntroductionProfileView view = views.get(0);
        assertThat(view.memberId()).isEqualTo(memberId);
        assertThat(view.profileImageUrl()).isEqualTo(profileImageUrl);
        assertThat(view.interviewAnswerContent()).isEqualTo(interviewAnswer);
        assertThat(view.likeLevel()).isEqualTo(likeLevel);
        assertThat(view.isIntroduced()).isEqualTo(isIntroduced);

        assertThat(view.hobbies()).containsExactlyInAnyOrderElementsOf(expectedHobbies);
        assertThat(view.religion()).isEqualTo(expectedReligion);
        assertThat(view.mbti()).isEqualTo(expectedMbti);
    }
}