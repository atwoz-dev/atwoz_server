package atwoz.atwoz.member;

import atwoz.atwoz.member.application.MemberService;
import atwoz.atwoz.member.application.dto.MemberProfileUpdateRequest;
import atwoz.atwoz.member.application.dto.MemberProfileUpdateResponse;
import atwoz.atwoz.member.domain.member.*;
import atwoz.atwoz.member.exception.InvalidMemberEnumValueException;
import atwoz.atwoz.member.exception.MemberNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MemberProfileUpdateTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @DisplayName("멤버를 찾을 수 없을 경우, 예외 발생")
    @Test
    void throwExceptionWhenMemberIsNotFound() {
        // Given
        Long memberId = 1L;

        // When
        Mockito.when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // Then
        Assertions.assertThatThrownBy(() -> memberService.updateMember(memberId, null)).isInstanceOf(MemberNotFoundException.class);
    }

    @DisplayName("멤버가 존재하지만 Enum 값이 적절하지 않은 경우, 예외 발생")
    @Test
    void throwExceptionWhenEnumValueIsInvalid() {
        // Given
        Long memberId = 1L;
        MemberProfileUpdateRequest invalidRequest = new MemberProfileUpdateRequest(
                "nickname", "INVALID_ENUM", 20, 180, // 잘못된 gender 값
                2L, "DAEJEON", "OTHER", "ENFJ",
                "DAILY", "ABSTINENT", "BUDDHISM", List.of(1L, 2L)
        );
        Member existingMember = Member.createFromPhoneNumber("01012345678");


        // Mock 동작 설정
        Mockito.when(memberRepository.findById(memberId)).thenReturn(Optional.of(existingMember));

        // When & Then
        Assertions.assertThatThrownBy(() -> memberService.updateMember(memberId, invalidRequest))
                .isInstanceOf(InvalidMemberEnumValueException.class);
    }

    @DisplayName("멤버가 존재하는 경우, 성공")
    @Test
    void succeedsWhenMemberIsFound() {
        // Given
        Long memberId = 1L;
        MemberProfileUpdateRequest request = new MemberProfileUpdateRequest(
                "nickname", "MALE", 20, 180,
                2L, "Daejeon", "OTHER", "ENFJ",
                "DAILY", "ABSTINENT", "BUDDHISM", List.of(1L, 2L)
        );
        Member existingMember = Member.createFromPhoneNumber("01012345678");

        // When
        Mockito.when(memberRepository.findById(memberId)).thenReturn(Optional.of(existingMember));

        // Then
        MemberProfileUpdateResponse response = memberService.updateMember(memberId, request);
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.memberProfile().getNickname().getNickname()).isEqualTo("nickname");
        Assertions.assertThat(response.memberProfile().getGender()).isEqualTo(Gender.MALE);
        Assertions.assertThat(response.memberProfile().getJobId()).isEqualTo(2L);
        Assertions.assertThat(response.memberProfile().getAge()).isEqualTo(20);
        Assertions.assertThat(response.memberProfile().getHeight()).isEqualTo(180);
        Assertions.assertThat(response.memberProfile().getRegion()).isEqualTo(Region.DAEJEON);
        Assertions.assertThat(response.memberProfile().getReligionStatus()).isEqualTo(ReligionStatus.BUDDHISM);

        Assertions.assertThat(response.memberProfile().getMemberHobbyList()).hasSize(2);
        for (MemberHobby memberHobby : response.memberProfile().getMemberHobbyList()) {
            Assertions.assertThat(memberHobby.getMemberId()).isEqualTo(memberId);
        }
    }

    @DisplayName("멤버가 존재하는 경우, 특정 값에 null 이 포함되어 있더라도 성공.")
    @Test
    void succeedsWhenNullValueExists() {
        // Given
        Long memberId = 1L;
        MemberProfileUpdateRequest request = new MemberProfileUpdateRequest(
                "nickname", "MALE", 20, 180,
                2L, null, "OTHER", "ENFJ",
                "DAILY", "ABSTINENT", "BUDDHISM", null
        );
        Member existingMember = Member.createFromPhoneNumber("01012345678");

        // When
        Mockito.when(memberRepository.findById(memberId)).thenReturn(Optional.of(existingMember));

        // Then
        MemberProfileUpdateResponse response = memberService.updateMember(memberId, request);
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.memberProfile().getNickname().getNickname()).isEqualTo("nickname");
        Assertions.assertThat(response.memberProfile().getGender()).isEqualTo(Gender.MALE);
        Assertions.assertThat(response.memberProfile().getJobId()).isEqualTo(2L);
        Assertions.assertThat(response.memberProfile().getAge()).isEqualTo(20);
        Assertions.assertThat(response.memberProfile().getHeight()).isEqualTo(180);
        Assertions.assertThat(response.memberProfile().getRegion()).isNull();
        Assertions.assertThat(response.memberProfile().getReligionStatus()).isEqualTo(ReligionStatus.BUDDHISM);

        Assertions.assertThat(response.memberProfile().getMemberHobbyList()).hasSize(0);

    }
}
