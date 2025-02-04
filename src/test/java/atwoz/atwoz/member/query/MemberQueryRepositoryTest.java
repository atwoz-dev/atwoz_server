package atwoz.atwoz.member.query;

import atwoz.atwoz.member.command.domain.member.vo.MemberProfile;
import atwoz.atwoz.member.query.member.MemberQueryRepository;
import atwoz.atwoz.member.query.member.dto.MemberContactResponse;
import atwoz.atwoz.member.query.member.dto.MemberProfileResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;


@DataJpaTest
@Import({TestConfig.class, MemberQueryRepository.class})
public class MemberQueryRepositoryTest {

    @Autowired
    private MemberQueryRepository memberQueryRepository;

    @Autowired
    private TestConfig.TestData testData;

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
        void isSuccessWhenMemberIsExists() {
            // Given
            Long existMemberId = testData.member().getId();

            // When
            MemberProfileResponse memberProfileResponse = memberQueryRepository.findProfileByMemberId(existMemberId).orElse(null);

            MemberProfile savedMemberProfile = testData.member().getProfile();

            // Then
            Assertions.assertThat(memberProfileResponse).isNotNull();
            Assertions.assertThat(memberProfileResponse.getAge()).isEqualTo(savedMemberProfile.getAge());
            Assertions.assertThat(memberProfileResponse.getHeight()).isEqualTo(savedMemberProfile.getHeight());
            Assertions.assertThat(memberProfileResponse.getDrinkingStatus()).isEqualTo(savedMemberProfile.getDrinkingStatus().toString());
            Assertions.assertThat(memberProfileResponse.getJob()).isEqualTo(testData.job().getName());
            Assertions.assertThat(memberProfileResponse.getHobbies().size()).isEqualTo(savedMemberProfile.getHobbyIds().size());
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
            Long existMemberId = testData.member().getId();

            // When
            MemberContactResponse memberContactResponse = memberQueryRepository.findContactsByMemberId(existMemberId).orElse(null);

            // Then
            Assertions.assertThat(memberContactResponse).isNotNull();
            Assertions.assertThat(memberContactResponse.getPhoneNumber()).isEqualTo(testData.member().getPhoneNumber());
            Assertions.assertThat(memberContactResponse.getKakaoId()).isEqualTo(testData.member().getKakaoId());
            Assertions.assertThat(memberContactResponse.getPrimaryContactType()).isEqualTo(testData.member().getPrimaryContactType().toString());
        }
    }
}
