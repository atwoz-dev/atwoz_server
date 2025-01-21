package atwoz.atwoz.member;

import atwoz.atwoz.hobby.domain.HobbyRepository;
import atwoz.atwoz.job.domain.JobRepository;
import atwoz.atwoz.member.application.MemberService;
import atwoz.atwoz.member.domain.member.Member;
import atwoz.atwoz.member.domain.member.MemberRepository;
import atwoz.atwoz.member.exception.KakaoIdAlreadyExistsException;
import atwoz.atwoz.member.exception.PhoneNumberAlreadyExistsException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MemberContactUpdateTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private HobbyRepository hobbyRepository;

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private MemberService memberService;

    @Nested
    @DisplayName("휴대폰 번호 변경 테스트")
    class PhoneNumberUpdateTest {

        @Test
        @DisplayName("변경한 핸드폰 번호가 다른 멤버가 사용중인 핸드폰 번호인 경우 실패.")
        void isFailWhenUpdatedNumberIsAnotherMemberPhoneNumber() {
            // Given
            String updatePhoneNumber = "01098765432";
            Member anotherMember = Member.createFromPhoneNumber("01098765432");


            Mockito.when(memberRepository.findByPhoneNumber(updatePhoneNumber)).thenReturn(Optional.of(anotherMember));

            // When & Then
            Assertions.assertThatThrownBy(() -> memberService.updatePhoneNumber(null, updatePhoneNumber))
                    .isInstanceOf(PhoneNumberAlreadyExistsException.class);
        }

        @Test
        @DisplayName("아무도 사용하지 않는 핸드폰 번호인 경우 성공.")
        void isSuccessWhenUpdateNumberIsNotAnotherMemberPhoneNumber() {
            // Given
            Member member = Member.createFromPhoneNumber("01012345678");
            String updatedPhoneNumber = "01098765432";

            Mockito.when(memberRepository.findByPhoneNumber(updatedPhoneNumber)).thenReturn(Optional.empty());
            Mockito.when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

            // When
            memberService.updatePhoneNumber(member.getId(), updatedPhoneNumber);

            // Then
            Assertions.assertThat(member.getPhoneNumber()).isEqualTo(updatedPhoneNumber);
        }
    }

    @Nested
    @DisplayName("카카오 아이디 업데이트 테스트")
    class KakaoIdUpdateTest {

        Member member = Member.createFromPhoneNumber("01012345678");
        Member anotherMember = Member.createFromPhoneNumber("01098765432");

        @Test
        @DisplayName("변경한 카카오 아이디가 다른 멤버가 사용중인 경우 실패.")
        void isFailWhenUpdatedKakaoIdIsAnotherMemberKakaoId() {
            // Given
            String kakaoId = "kakaoId";
            Mockito.when(memberRepository.findByKakaoId(kakaoId)).thenReturn(Optional.of(anotherMember));

            Assertions.assertThat(member.getId()).isEqualTo(anotherMember.getId());

            // When & Then
//            Assertions.assertThatThrownBy(() -> memberService.updateKakaoId(member.getId(), kakaoId))
//                    .isInstanceOf(KakaoIdAlreadyExistsException.class);
        }

        @Test
        @DisplayName("아무도 사용하지 않은 카카오 아디인 경우 성공.")
        void isSuccessWhenUpdatedKakaoIdIsNotAnotherMemberKakaoId() {

        }
    }

    @Test
    @DisplayName("자신의 등록된 연락처 조회 테스트")
    void test() {

    }

}
