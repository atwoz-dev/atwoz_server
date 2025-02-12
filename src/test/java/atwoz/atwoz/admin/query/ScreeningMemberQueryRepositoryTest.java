package atwoz.atwoz.admin.query;

import atwoz.atwoz.QuerydslConfig;
import atwoz.atwoz.admin.command.domain.memberscreening.MemberScreening;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.vo.MemberProfile;
import atwoz.atwoz.member.command.domain.member.vo.Nickname;
import atwoz.atwoz.member.command.domain.member.vo.PhoneNumber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QuerydslConfig.class, ScreeningMemberQueryRepository.class})
class ScreeningMemberQueryRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ScreeningMemberQueryRepository screeningMemberQueryRepository;

    @Test
    @DisplayName("조건 없이 전체 심사를 조회합니다.")
    void findAllScreenings() {
        // given
        Member member1 = createMember("member1", "01011111111");
        Member member2 = createMember("member2", "01022222222");
        Member member3 = createMember("member3", "01033333333");
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);

        MemberScreening screening1 = MemberScreening.from(member1.getId());
        MemberScreening screening2 = MemberScreening.from(member2.getId());
        em.persist(screening1);
        em.persist(screening2);

        em.flush();
        em.clear();

        ScreeningSearchCondition condition = new ScreeningSearchCondition(null, null, null, null, null);
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<ScreeningMember> screeningMembers = screeningMemberQueryRepository.findScreeningMembers(condition, pageRequest);

        // then
        assertThat(screeningMembers.getTotalElements()).isEqualTo(2);
        assertThat(screeningMembers.getContent()).extracting("nickname")
                .containsExactlyInAnyOrder("member1", "member2");
    }

    @Test
    @DisplayName("대기 중인 심사를 조회합니다.")
    void findPendingScreenings() {
        // given
        Member member1 = createMember("member1", "01011111111");
        Member member2 = createMember("member2", "01022222222");
        em.persist(member1);
        em.persist(member2);

        MemberScreening screening1 = MemberScreening.from(member1.getId());
        MemberScreening screening2 = MemberScreening.from(member2.getId());
        em.persist(screening1);
        em.persist(screening2);

        em.flush();
        em.clear();

        ScreeningSearchCondition condition = new ScreeningSearchCondition("PENDING", null, null, null, null);
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<ScreeningMember> screeningMembers = screeningMemberQueryRepository.findScreeningMembers(condition, pageRequest);

        // then
        assertThat(screeningMembers.getTotalElements()).isEqualTo(2);
        assertThat(screeningMembers.getContent()).extracting("nickname")
                .containsExactlyInAnyOrder("member1", "member2");
    }

    @Test
    @DisplayName("승인된 심사를 조회합니다.")
    void findApprovedScreenings() {
        // given
        Member member1 = createMember("member1", "01011111111");
        Member member2 = createMember("member2", "01022222222");
        em.persist(member1);
        em.persist(member2);

        MemberScreening screening1 = MemberScreening.from(member1.getId());
        MemberScreening screening2 = MemberScreening.from(member2.getId());
        em.persist(screening1);
        em.persist(screening2);

        em.flush();
        em.clear();

        ScreeningSearchCondition condition = new ScreeningSearchCondition("APPROVED", null, null, null, null);
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<ScreeningMember> screeningMembers = screeningMemberQueryRepository.findScreeningMembers(condition, pageRequest);

        // then
        assertThat(screeningMembers.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("닉네임으로 심사를 조회합니다.")
    void findScreeningsByNickname() {
        // given
        Member member1 = createMember("member1", "01011111111");
        Member member2 = createMember("member2", "01022222222");
        em.persist(member1);
        em.persist(member2);

        MemberScreening screening1 = MemberScreening.from(member1.getId());
        MemberScreening screening2 = MemberScreening.from(member2.getId());
        em.persist(screening1);
        em.persist(screening2);

        em.flush();
        em.clear();

        ScreeningSearchCondition condition = new ScreeningSearchCondition(null, "member1", null, null, null);
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<ScreeningMember> screeningMembers = screeningMemberQueryRepository.findScreeningMembers(condition, pageRequest);

        // then
        assertThat(screeningMembers.getTotalElements()).isEqualTo(1);
        assertThat(screeningMembers.getContent().getFirst().nickname()).isEqualTo("member1");
    }

    @Test
    @DisplayName("전화번호로 심사를 조회합니다.")
    void findScreeningsByPhoneNumber() {
        // given
        Member member1 = createMember("member1", "01011111111");
        Member member2 = createMember("member2", "01022222222");
        em.persist(member1);
        em.persist(member2);

        MemberScreening screening1 = MemberScreening.from(member1.getId());
        MemberScreening screening2 = MemberScreening.from(member2.getId());
        em.persist(screening1);
        em.persist(screening2);

        em.flush();
        em.clear();

        ScreeningSearchCondition condition = new ScreeningSearchCondition(null, null, "01011111111", null, null);
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<ScreeningMember> screeningMembers = screeningMemberQueryRepository.findScreeningMembers(condition, pageRequest);

        // then
        assertThat(screeningMembers.getTotalElements()).isEqualTo(1);
        assertThat(screeningMembers.getContent().getFirst().nickname()).isEqualTo("member1");
    }

    @Test
    @DisplayName("시작일과 종료일을 설정해 심사를 조회합니다.")
    void findScreeningsByStartDateAndEndDate() {
        // given
        Member member1 = createMember("member1", "01011111111");
        Member member2 = createMember("member2", "01022222222");
        em.persist(member1);
        em.persist(member2);

        MemberScreening screening1 = MemberScreening.from(member1.getId());
        MemberScreening screening2 = MemberScreening.from(member2.getId());
        em.persist(screening1);
        em.persist(screening2);

        em.flush();
        em.clear();

        ScreeningSearchCondition condition = new ScreeningSearchCondition(
                null,
                null,
                null,
                LocalDate.EPOCH,
                LocalDate.now().plusDays(1)
        );
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<ScreeningMember> screeningMembers = screeningMemberQueryRepository.findScreeningMembers(condition, pageRequest);

        // then
        assertThat(screeningMembers.getTotalElements()).isEqualTo(2);
        assertThat(screeningMembers.getContent()).extracting("nickname")
                .containsExactlyInAnyOrder("member1", "member2");
    }

    private Member createMember(String nickname, String phoneNumber) {
        return Member.builder()
                .phoneNumber(PhoneNumber.from(phoneNumber))
                .profile(
                        MemberProfile.builder()
                                .nickname(Nickname.from(nickname))
                                .build()
                )
                .build();
    }
}