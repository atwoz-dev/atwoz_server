package atwoz.atwoz.admin.query;

import atwoz.atwoz.QuerydslConfig;
import atwoz.atwoz.admin.command.domain.screening.Screening;
import atwoz.atwoz.interview.command.domain.answer.InterviewAnswer;
import atwoz.atwoz.interview.command.domain.question.InterviewCategory;
import atwoz.atwoz.interview.command.domain.question.InterviewQuestion;
import atwoz.atwoz.member.command.domain.member.Gender;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.vo.MemberProfile;
import atwoz.atwoz.member.command.domain.member.vo.Nickname;
import atwoz.atwoz.member.command.domain.member.vo.PhoneNumber;
import atwoz.atwoz.member.command.domain.profileImage.ProfileImage;
import atwoz.atwoz.member.command.domain.profileImage.vo.ImageUrl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QuerydslConfig.class, ScreeningDetailQueryRepository.class})
class ScreeningDetailQueryRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ScreeningDetailQueryRepository screeningDetailQueryRepository;

    @Test
    @DisplayName("상세 심사를 조회합니다.")
    void findById() {
        // given
        Member member = createMember();
        long memberId = (Long) em.persistAndGetId(member);

        Screening screening = Screening.from(memberId);
        long screeningId = (Long) em.persistAndGetId(screening);

        ProfileImage profileImage1 = createProfileImage(memberId, "image_url_1", true);
        ProfileImage profileImage2 = createProfileImage(memberId, "image_url_2", false);
        em.persist(profileImage1);
        em.persist(profileImage2);

        InterviewQuestion question1 = createInterviewQuestion("question1", true);
        InterviewQuestion question2 = createInterviewQuestion("question2", false);
        InterviewQuestion question3 = createInterviewQuestion("question3", true);
        long question1Id = (Long) em.persistAndGetId(question1);
        long question2Id = (Long) em.persistAndGetId(question2);
        em.persist(question3);

        InterviewAnswer answer1 = InterviewAnswer.of(question1Id, memberId, "answer for question1");
        InterviewAnswer answer2 = InterviewAnswer.of(question2Id, memberId, "answer for question2");
        em.persist(answer1);
        em.persist(answer2);

        em.flush();
        em.clear();

        // when
        ScreeningDetailView screeningDetail = screeningDetailQueryRepository.findById(screeningId);

        // then
        assertThat(screeningDetail).isNotNull();

        assertThat(screeningDetail.screeningId()).isEqualTo(screening.getId());
        assertThat(screeningDetail.profile().nickname()).isEqualTo("member");
        assertThat(screeningDetail.profile().gender()).isEqualTo("MALE");
        assertThat(screeningDetail.profile().screeningStatus()).isEqualTo(screening.getStatus().toString());
        assertThat(screeningDetail.profile().rejectionReason()).isNull();

        assertThat(screeningDetail.profileImages()).hasSize(2);
        assertThat(screeningDetail.profileImages()).extracting("imageUrl")
                .containsExactlyInAnyOrder("image_url_1", "image_url_2");
        assertThat(screeningDetail.profileImages()).extracting("isPrimary")
                .containsExactlyInAnyOrder(true, false);

        assertThat(screeningDetail.interviews()).hasSize(1);
        assertThat(screeningDetail.interviews().getFirst().question()).isEqualTo("question1");
        assertThat(screeningDetail.interviews().getFirst().answer()).isEqualTo("answer for question1");
    }

    private Member createMember() {
        return Member.builder()
                .phoneNumber(PhoneNumber.from("01011111111"))
                .profile(
                        MemberProfile.builder()
                                .nickname(Nickname.from("member"))
                                .yearOfBirth(20)
                                .gender(Gender.MALE)
                                .build()
                )
                .build();
    }

    private ProfileImage createProfileImage(long memberId, String imageUrl, boolean isPrimary) {
        return ProfileImage.builder()
                .memberId(memberId)
                .imageUrl(ImageUrl.from(imageUrl))
                .isPrimary(isPrimary)
                .build();
    }

    private InterviewQuestion createInterviewQuestion(String question1, boolean isPublic) {
        return InterviewQuestion.of(question1, InterviewCategory.PERSONAL, isPublic);
    }
}