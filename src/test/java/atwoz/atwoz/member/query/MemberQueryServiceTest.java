package atwoz.atwoz.member.query;

import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.command.domain.member.vo.MemberProfile;
import atwoz.atwoz.member.query.member.MemberQueryRepository;
import atwoz.atwoz.member.query.member.MemberQueryService;
import atwoz.atwoz.member.query.member.dto.MemberContactResponse;
import atwoz.atwoz.member.query.member.dto.MemberProfileResponse;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;


@DataJpaTest
@Import({TestConfig.class, MemberQueryService.class, MemberQueryRepository.class})
public class MemberQueryServiceTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MemberQueryService memberQueryService;

    @Autowired
    private TestConfig.TestData testData;

    @Nested
    @DisplayName("프로필 조회 테스트")
    class ProfileQueryTest {

        @Test
        @DisplayName("존재하지 않은 아이디인 경우, 프로필 조회 실패.")
        void isFailWhenMemberIsNotExists() {
            // Given
            Long notExistMemberId = 10L;

            // When & Then
            Assertions.assertThatThrownBy(() -> memberQueryService.getProfile(notExistMemberId))
                    .isInstanceOf(MemberNotFoundException.class);
        }

        @Test
        @DisplayName("존재하는 아이디인 경우, 프로필 조회 성공.")
        void isSuccessWhenMemberIsExists() {
            // Given
            Long existMemberId = testData.getMember().getId();

            // When
            MemberProfileResponse memberProfileResponse = memberQueryService.getProfile(existMemberId);

            MemberProfile savedMemberProfile = testData.getMember().getProfile();

            // Then
            Assertions.assertThat(memberProfileResponse).isNotNull();
            Assertions.assertThat(memberProfileResponse.age()).isEqualTo(savedMemberProfile.getAge());
            Assertions.assertThat(memberProfileResponse.height()).isEqualTo(savedMemberProfile.getHeight());
            Assertions.assertThat(memberProfileResponse.drinkingStatus()).isEqualTo(savedMemberProfile.getDrinkingStatus().toString());
            Assertions.assertThat(memberProfileResponse.job()).isEqualTo(testData.getJob().getName());
            Assertions.assertThat(memberProfileResponse.hobbies().size()).isEqualTo(savedMemberProfile.getHobbyIds().size());
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
            Assertions.assertThatThrownBy(() -> memberQueryService.getContacts(notExistMemberId))
                    .isInstanceOf(MemberNotFoundException.class);
        }


        @Test
        @DisplayName("아이디가 존재하는 경우 연락처 조회 성공.")
        void isSuccessWhenMemberIsExists() {
            // Given
            Long existMemberId = testData.getMember().getId();

            // When
            MemberContactResponse memberContactResponse = memberQueryService.getContacts(existMemberId);

            // Then
            Assertions.assertThat(memberContactResponse).isNotNull();
            Assertions.assertThat(memberContactResponse.getPhoneNumber()).isEqualTo(testData.getMember().getPhoneNumber());
            Assertions.assertThat(memberContactResponse.getKakaoId()).isEqualTo(testData.getMember().getKakaoId());
            Assertions.assertThat(memberContactResponse.getPrimaryContactType()).isEqualTo(testData.getMember().getPrimaryContactType().toString());
        }
    }
}
