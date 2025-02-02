package atwoz.atwoz.member.application;

import atwoz.atwoz.hobby.command.domain.HobbyRepository;
import atwoz.atwoz.job.command.domain.JobRepository;
import atwoz.atwoz.member.application.dto.MemberContactResponse;
import atwoz.atwoz.member.application.exception.KakaoIdAlreadyExistsException;
import atwoz.atwoz.member.application.exception.MemberNotFoundException;
import atwoz.atwoz.member.application.exception.PhoneNumberAlreadyExistsException;
import atwoz.atwoz.member.domain.member.KakaoId;
import atwoz.atwoz.member.domain.member.Member;
import atwoz.atwoz.member.domain.member.MemberRepository;
import atwoz.atwoz.member.domain.member.PrimaryContactType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MemberContactServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private HobbyRepository hobbyRepository;

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private MemberContactService memberContactService;

    @Nested
    class GetTest {
        @Test
        @DisplayName("존재하지 않은 아이디의 경우 연락처 조회 실패.")
        void isFailWhenMemberIsNotExists() {
            // Given
            String phoneNumber = "01012345678";
            Member member = Member.fromPhoneNumber(phoneNumber);

            Mockito.when(memberRepository.findById(Mockito.any())).thenReturn(Optional.empty());

            // When
            Assertions.assertThatThrownBy(() -> memberContactService.getContacts(member.getId()))
                    .isInstanceOf(MemberNotFoundException.class);
        }

        @Test
        @DisplayName("아이디가 존재하는 경우 연락처 조회 성공.")
        void isSuccessWhenMemberIsExists() {
            // Given
            String phoneNumber = "01012345678";
            String kakaoId = "kakaoId";
            Member member = Member.fromPhoneNumber(phoneNumber);
            member.changePrimaryContactTypeToKakao(KakaoId.from(kakaoId));

            Mockito.when(memberRepository.findById(Mockito.any())).thenReturn(Optional.of(member));

            // When
            MemberContactResponse memberContactResponse = memberContactService.getContacts(member.getId());

            // Then
            Assertions.assertThat(memberContactResponse).isNotNull();
            Assertions.assertThat(memberContactResponse.phoneNumber()).isEqualTo(phoneNumber);
            Assertions.assertThat(memberContactResponse.kakaoId()).isEqualTo(kakaoId);
            Assertions.assertThat(memberContactResponse.primaryContactType()).isEqualTo(PrimaryContactType.KAKAO.toString());
        }
    }

    @Nested
    class UpdateTest {
        @Nested
        @DisplayName("휴대폰 번호 변경 테스트")
        class PhoneNumberUpdateTest {

            Member member = Member.fromPhoneNumber("01012345678");
            Member anotherMember = Member.fromPhoneNumber("01098765432");

            @Test
            @DisplayName("변경한 핸드폰 번호가 다른 멤버가 사용중인 핸드폰 번호인 경우 실패.")
            void isFailWhenUpdatedNumberIsAnotherMemberPhoneNumber() {
                // Given
                String updatePhoneNumber = anotherMember.getPhoneNumber();
                ReflectionTestUtils.setField(member, "id", 1L);

                Mockito.when(memberRepository.existsByPhoneNumberAndIdNot(Mockito.any(), Mockito.any())).thenReturn(true);

                // When & Then
                Assertions.assertThatThrownBy(() -> memberContactService.updatePhoneNumber(member.getId(), updatePhoneNumber))
                        .isInstanceOf(PhoneNumberAlreadyExistsException.class);
            }

            @Test
            @DisplayName("아무도 사용하지 않는 핸드폰 번호인 경우 성공.")
            void isSuccessWhenUpdateNumberIsNotAnotherMemberPhoneNumber() {
                // Given
                String updatedPhoneNumber = "01099999999";

                Mockito.when(memberRepository.existsByPhoneNumberAndIdNot(Mockito.any(), Mockito.any())).thenReturn(false);
                Mockito.when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

                // When
                memberContactService.updatePhoneNumber(member.getId(), updatedPhoneNumber);

                // Then
                Assertions.assertThat(member.getPhoneNumber()).isEqualTo(updatedPhoneNumber);
            }

            @Test
            @DisplayName("기존 자신의 핸드폰 번호인 경우 성공.")
            void isSuccessWhenUpdateNumberIsOwnPhoneNumber() {
                // Given
                String updatedPhoneNumber = member.getPhoneNumber();

                Mockito.when(memberRepository.existsByPhoneNumberAndIdNot(Mockito.any(), Mockito.any())).thenReturn(false);
                Mockito.when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

                // When
                memberContactService.updatePhoneNumber(member.getId(), updatedPhoneNumber);

                // Then
                Assertions.assertThat(member.getPhoneNumber()).isEqualTo(updatedPhoneNumber);
            }
        }

        @Nested
        @DisplayName("카카오 아이디 업데이트 테스트")
        class KakaoIdUpdateTest {

            Member member = Member.fromPhoneNumber("01012345678");
            Member anotherMember = Member.fromPhoneNumber("01098765432");

            @Test
            @DisplayName("변경한 카카오 아이디가 다른 멤버가 사용중인 경우 실패.")
            void isFailWhenUpdatedKakaoIdIsAnotherMemberKakaoId() {
                // Given
                String kakaoId = "kakaoId";

                Mockito.when(memberRepository.existsByKakaoIdAndIdNot(Mockito.any(), Mockito.any())).thenReturn(true);

                // When & Then
                Assertions.assertThatThrownBy(() -> memberContactService.updateKakaoId(member.getId(), kakaoId))
                        .isInstanceOf(KakaoIdAlreadyExistsException.class);
            }

            @Test
            @DisplayName("아무도 사용하지 않은 카카오 아이디인 경우 성공.")
            void isSuccessWhenUpdatedKakaoIdIsNotAnotherMemberKakaoId() {
                // Given
                String kakaoId = "kakaoId";

                Mockito.when(memberRepository.existsByKakaoIdAndIdNot(Mockito.any(), Mockito.any())).thenReturn(false);
                Mockito.when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

                // When
                memberContactService.updateKakaoId(member.getId(), kakaoId);

                // Then
                Assertions.assertThat(member.getKakaoId()).isEqualTo(kakaoId);
            }

            @Test
            @DisplayName("기존 자신의 카카오 아이디인 경우 성공.")
            void isSuccessWhenUpdatedKakaoIdIsOwnKakaoId() {
                // Given
                String kakaoId = "kakaoId";
                member.changePrimaryContactTypeToKakao(KakaoId.from(kakaoId));

                Mockito.when(memberRepository.existsByKakaoIdAndIdNot(Mockito.any(), Mockito.any())).thenReturn(false);
                Mockito.when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

                // When
                memberContactService.updateKakaoId(member.getId(), kakaoId);

                // Then
                Assertions.assertThat(member.getKakaoId()).isEqualTo(kakaoId);
            }
        }
    }
}
