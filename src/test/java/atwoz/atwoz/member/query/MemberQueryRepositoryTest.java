package atwoz.atwoz.member.query;

import atwoz.atwoz.QuerydslConfig;
import atwoz.atwoz.admin.command.domain.hobby.Hobby;
import atwoz.atwoz.admin.command.domain.job.Job;
import atwoz.atwoz.member.command.domain.member.DrinkingStatus;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.vo.KakaoId;
import atwoz.atwoz.member.command.domain.member.vo.MemberProfile;
import atwoz.atwoz.member.query.member.MemberQueryRepository;
import atwoz.atwoz.member.query.member.view.MemberContactView;
import atwoz.atwoz.member.query.member.view.MemberProfileView;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;


@DataJpaTest
@Import({QuerydslConfig.class, MemberQueryRepository.class})
public class MemberQueryRepositoryTest {

    @Autowired
    private MemberQueryRepository memberQueryRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Nested
    @DisplayName("프로필 조회 테스트")
    class ProfileQueryTest {

        @Test
        @DisplayName("존재하지 않은 아이디인 경우, 프로필 빈 값 반환.")
        void isNullWhenMemberIsNotExists() {
            // Given
            Long notExistMemberId = 10L;

            // When & Then
            Assertions.assertThat(memberQueryRepository.findContactsByMemberId(notExistMemberId).orElse(null)).isNull();
        }

        @Test
        @DisplayName("존재하는 아이디인 경우, 프로필 조회 성공.")
        @Transactional
        void isSuccessWhenMemberIsExists() {
            // Given
            Job job = Job.from("직업1");
            Hobby hobby1 = Hobby.from("취미1");
            Hobby hobby2 = Hobby.from("취미2");
            entityManager.persist(job);
            entityManager.persist(hobby1);
            entityManager.persist(hobby2);

            entityManager.flush();

            Member member = Member.fromPhoneNumber("01012345678");
            MemberProfile updateProfile = MemberProfile.builder()
                    .age(10)
                    .height(20)
                    .drinkingStatus(DrinkingStatus.NONE)
                    .jobId(job.getId())
                    .hobbyIds(Set.of(hobby1.getId(), hobby2.getId()))
                    .build();

            member.updateProfile(updateProfile);
            entityManager.persist(member);
            entityManager.flush();

            // When
            MemberProfileView memberProfileView = memberQueryRepository.findProfileByMemberId(member.getId()).orElse(null);

            // Then
            MemberProfile savedMemberProfile = member.getProfile();
            Assertions.assertThat(memberProfileView).isNotNull();
            Assertions.assertThat(memberProfileView.age()).isEqualTo(savedMemberProfile.getAge());
            Assertions.assertThat(memberProfileView.height()).isEqualTo(savedMemberProfile.getHeight());
            Assertions.assertThat(memberProfileView.drinkingStatus()).isEqualTo(savedMemberProfile.getDrinkingStatus().toString());
            Assertions.assertThat(memberProfileView.job()).isEqualTo(job.getName());
            Assertions.assertThat(memberProfileView.hobbies().size()).isEqualTo(savedMemberProfile.getHobbyIds().size());
        }
    }

    @Nested
    @DisplayName("연락처 조회 테스트")
    class ContactQueryTest {

        @Test
        @DisplayName("존재하지 않은 아이디의 경우 연락처 조회 실패.")
        void isFailWhenMemberIsNotExists() {
            // Given
            Long notExistMemberId = 10L;

            // When & Then
            Assertions.assertThat(memberQueryRepository.findContactsByMemberId(notExistMemberId).orElse(null)).isNull();
        }


        @Test
        @DisplayName("아이디가 존재하는 경우 연락처 조회 성공.")
        void isSuccessWhenMemberIsExists() {
            // Given
            Member member = Member.fromPhoneNumber("01012345678");
            member.changePrimaryContactTypeToKakao(KakaoId.from("kakaoId"));
            entityManager.persist(member);
            entityManager.flush();

            Long existMemberId = member.getId();

            // When
            MemberContactView memberContactView = memberQueryRepository.findContactsByMemberId(existMemberId).orElse(null);

            // Then
            Assertions.assertThat(memberContactView).isNotNull();
            Assertions.assertThat(memberContactView.phoneNumber()).isEqualTo(member.getPhoneNumber());
            Assertions.assertThat(memberContactView.kakaoId()).isEqualTo(member.getKakaoId());
            Assertions.assertThat(memberContactView.primaryContactType()).isEqualTo(member.getPrimaryContactType().toString());
        }
    }
}
