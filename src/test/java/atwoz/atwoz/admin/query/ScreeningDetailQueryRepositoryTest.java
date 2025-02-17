package atwoz.atwoz.admin.query;

import atwoz.atwoz.QuerydslConfig;
import atwoz.atwoz.admin.command.domain.screening.Screening;
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
    @DisplayName("심사를 상세 조회합니다.")
    void findById() {
        // given
        Member member = createMember();
        em.persist(member);

        Screening screening = Screening.from(member.getId());
        em.persist(screening);

        ProfileImage profileImage1 = createProfileImage(member.getId(), "image_url_1", true);
        ProfileImage profileImage2 = createProfileImage(member.getId(), "image_url_2", false);
        em.persist(profileImage1);
        em.persist(profileImage2);

        em.flush();
        em.clear();

        // when
        ScreeningDetailView screeningDetail = screeningDetailQueryRepository.findById(screening.getId());

        System.out.println(screeningDetail);

        // then
        assertThat(screeningDetail).isNotNull();

        assertThat(screeningDetail.screeningId()).isEqualTo(screening.getId());
        assertThat(screeningDetail.nickname()).isEqualTo("member");
        assertThat(screeningDetail.gender()).isEqualTo("MALE");
        assertThat(screeningDetail.screeningStatus()).isEqualTo(screening.getStatus().toString());
        assertThat(screeningDetail.rejectionReason()).isNull();

        assertThat(screeningDetail.profileImages()).hasSize(2);
        assertThat(screeningDetail.profileImages()).extracting("imageUrl")
                .containsExactlyInAnyOrder("image_url_1", "image_url_2");
        assertThat(screeningDetail.profileImages()).extracting("isPrimary")
                .containsExactlyInAnyOrder(true, false);
    }

    private Member createMember() {
        return Member.builder()
                .phoneNumber(PhoneNumber.from("01011111111"))
                .profile(
                        MemberProfile.builder()
                                .nickname(Nickname.from("member"))
                                .age(20)
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
}