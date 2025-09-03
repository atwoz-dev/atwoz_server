package atwoz.atwoz.datingexam.adapter.database;

import atwoz.atwoz.QuerydslConfig;
import atwoz.atwoz.datingexam.domain.DatingExamAnswerEncoder;
import atwoz.atwoz.datingexam.domain.DatingExamSubmit;
import atwoz.atwoz.datingexam.domain.dto.DatingExamSubmitRequest;
import atwoz.atwoz.member.command.domain.member.ActivityStatus;
import atwoz.atwoz.member.command.domain.member.Gender;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.vo.MemberProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Import({QuerydslConfig.class, SoulmateQueryRepositoryImpl.class})
@DataJpaTest
class SoulmateQueryRepositoryImplTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private SoulmateQueryRepositoryImpl soulmateQueryRepository;

    @Nested
    @DisplayName("소울 메이트 아이디 목록을 조회할 때,")
    class FindSoulmateIds {
        private final DatingExamAnswerEncoder encoder = mock(DatingExamAnswerEncoder.class);

        private Member createMemberAndSubmit(String phoneNumber, Gender gender, boolean isProfilePublic,
            ActivityStatus activityStatus, String answer) {
            Member member = Member.fromPhoneNumber(phoneNumber);
            MemberProfile profile = MemberProfile.builder()
                .gender(gender)
                .build();
            member.updateProfile(profile);
            if (isProfilePublic) {
                member.publishProfile();
            }
            member.changeActivityStatus(activityStatus);

            em.persist(member);
            em.flush();

            DatingExamSubmitRequest request = mock(DatingExamSubmitRequest.class);
            when(request.subjectId()).thenReturn(100L);
            when(encoder.encode(request)).thenReturn(answer);
            DatingExamSubmit datingExamSubmit = DatingExamSubmit.from(request, encoder, member.getId());

            em.persist(datingExamSubmit);
            em.flush();
            return member;
        }

        @Test
        @DisplayName("연애 모의고사 필수 과목 제출 답안이 일치하고, 성별이 다르며, 프로필이 공개 상태이고, 활동 상태가 ACTIVE인 멤버들의 아이디를 조회한다.")
        void findOnlyOppositeGenderPublicActiveMembersWithSameRequiredAnswers() {
            // given
            Gender gender = Gender.MALE;
            boolean isProfilePublic = true;
            ActivityStatus activityStatus = ActivityStatus.ACTIVE;
            String sameAnswer = "sameAnswer";

            // 소울 메이트 아이디 조회 요청한 멤버
            Member requester = createMemberAndSubmit("01000000000", gender, isProfilePublic, activityStatus,
                sameAnswer);
            requester.markDatingExamSubmitted();
            em.persist(requester);
            em.flush();

            // 소울 메이트로 조회될 멤버
            Member soulmateMember = createMemberAndSubmit("01000000001", gender.getOpposite(), isProfilePublic,
                activityStatus, sameAnswer);

            // 필수과목 답안이 달라 소울 메이트가 아닌 멤버
            Member differentAnswerMember = createMemberAndSubmit("01000000002", gender.getOpposite(), isProfilePublic,
                activityStatus, "differentAnswer");

            // 같은 성별이라 소울 메이트에서 제외될 멤버
            Member sameGenderMember = createMemberAndSubmit("01000000003", gender, isProfilePublic, activityStatus,
                sameAnswer);

            // 프로필 비공개 상태라 소울 메이트에서 제외될 멤버
            Member profilePublicFalseMember = createMemberAndSubmit("01000000004", gender.getOpposite(), false,
                activityStatus, sameAnswer);

            // 활동 상태가 ACTIVE가 아니라서 소울 메이트에서 제외될 멤버
            Member inactiveMember = createMemberAndSubmit("01000000005", gender.getOpposite(), isProfilePublic,
                ActivityStatus.DORMANT, sameAnswer);

            // when
            Set<Long> soulmateIds = soulmateQueryRepository.findSameAnswerMemberIds(requester.getId());

            // then
            assertThat(soulmateIds).containsOnly(soulmateMember.getId());
        }

        @Test
        @DisplayName("연애 모의고사 필수 과목 제출 기록이 없다면 예외를 던진다.")
        void throwsExceptionWhenNoDatingExamSubmit() {
            // given
            Member member = Member.fromPhoneNumber("01000000000");
            em.persist(member);
            em.flush();

            // when & then
            try {
                soulmateQueryRepository.findSameAnswerMemberIds(member.getId());
            } catch (IllegalStateException e) {
                assertThat(e.getMessage()).contains("연애 모의고사 제출 기록이 없습니다");
            }
        }
    }
}