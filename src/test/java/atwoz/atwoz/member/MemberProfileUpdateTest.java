package atwoz.atwoz.member;

import atwoz.atwoz.hobby.domain.HobbyRepository;
import atwoz.atwoz.job.domain.JobRepository;
import atwoz.atwoz.job.exception.JobNotFoundException;
import atwoz.atwoz.member.application.MemberService;
import atwoz.atwoz.member.application.dto.MemberProfileUpdateRequest;
import atwoz.atwoz.member.application.dto.MemberProfileUpdateResponse;
import atwoz.atwoz.member.domain.member.*;
import atwoz.atwoz.member.exception.InvalidHobbyIdException;
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

    @Mock
    private HobbyRepository hobbyRepository;

    @Mock
    private JobRepository jobRepository;

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
        Long jobId = 2L;
        List<Long> hobbyIds = List.of(1L, 2L);

        MemberProfileUpdateRequest invalidRequest = new MemberProfileUpdateRequest(
                "nickname", "INVALID_ENUM", 20, 180, // 잘못된 gender 값
                jobId, "Daejeon", "OTHER", "ENFJ",
                "DAILY_SMOKER", "QUITTING", "BUDDHIST", hobbyIds
        );
        Member existingMember = Member.fromPhoneNumber("01012345678");


        // Mock 동작 설정
        Mockito.when(memberRepository.findById(memberId)).thenReturn(Optional.of(existingMember));
        Mockito.when(jobRepository.existsById(jobId)).thenReturn(true);
        Mockito.when(hobbyRepository.countHobbiesByIdIn(hobbyIds)).thenReturn(2L);

        // When & Then
        Assertions.assertThatThrownBy(() -> memberService.updateMember(memberId, invalidRequest))
                .isInstanceOf(InvalidMemberEnumValueException.class);
    }

    @DisplayName("멤버가 존재하지만 유효하지 않은 직업 ID를 가질 경우, 예외 발생")
    @Test
    void throwExceptionWhenJobIdIsInvalid() {
        // Given
        Long memberId = 1L;
        Long jobId = 2L;
        List<Long> hobbyIds = List.of(1L, 2L);

        MemberProfileUpdateRequest invalidRequest = new MemberProfileUpdateRequest(
                "nickname", "INVALID_ENUM", 20, 180, // 잘못된 gender 값
                jobId, "Daejeon", "OTHER", "ENFJ",
                "DAILY_SMOKER", "QUITTING", "BUDDHIST", hobbyIds
        );
        Member existingMember = Member.fromPhoneNumber("01012345678");


        // Mock 동작 설정
        Mockito.when(memberRepository.findById(memberId)).thenReturn(Optional.of(existingMember));
        Mockito.when(jobRepository.existsById(jobId)).thenReturn(false);

        // When & Then
        Assertions.assertThatThrownBy(() -> memberService.updateMember(memberId, invalidRequest))
                .isInstanceOf(JobNotFoundException.class);
    }

    @DisplayName("멤버가 존재하지만 유효하지 않은 취미 ID를 가질 경우, 예외 발생")
    @Test
    void throwExceptionWhenHobbyIdIsInvalid() {
        // Given
        Long memberId = 1L;
        Long jobId = 2L;
        List<Long> hobbyIds = List.of(1L, 2L);

        MemberProfileUpdateRequest invalidRequest = new MemberProfileUpdateRequest(
                "nickname", "INVALID_ENUM", 20, 180, // 잘못된 gender 값
                jobId, "Daejeon", "OTHER", "ENFJ",
                "DAILY_SMOKER", "QUITTING", "BUDDHIST", hobbyIds
        );
        Member existingMember = Member.fromPhoneNumber("01012345678");


        // Mock 동작 설정
        Mockito.when(memberRepository.findById(memberId)).thenReturn(Optional.of(existingMember));
        Mockito.when(jobRepository.existsById(jobId)).thenReturn(true);
        Mockito.when(hobbyRepository.countHobbiesByIdIn(hobbyIds)).thenReturn(1L);

        // When & Then
        Assertions.assertThatThrownBy(() -> memberService.updateMember(memberId, invalidRequest))
                .isInstanceOf(InvalidHobbyIdException.class);
    }

    @DisplayName("멤버가 존재하는 경우, 성공")
    @Test
    void succeedsWhenMemberIsFound() {
        // Given
        Long memberId = 1L;
        Long jobId = 2L;
        List<Long> hobbyIds = List.of(1L, 2L);
        MemberProfileUpdateRequest request = new MemberProfileUpdateRequest(
                "nickname", "MALE", 20, 180,
                jobId, "Daejeon", "OTHER", "ENFJ",
                "DAILY_SMOKER", "QUITTING", "BUDDHIST", hobbyIds
        );
        Member existingMember = Member.fromPhoneNumber("01012345678");

        // When
        Mockito.when(memberRepository.findById(memberId)).thenReturn(Optional.of(existingMember));
        Mockito.when(hobbyRepository.countHobbiesByIdIn(hobbyIds)).thenReturn(2L);
        Mockito.when(jobRepository.existsById(jobId)).thenReturn(true);

        // Then
        MemberProfileUpdateResponse response = memberService.updateMember(memberId, request);
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.memberProfile().getNickname().getValue()).isEqualTo("nickname");
        Assertions.assertThat(response.memberProfile().getGender()).isEqualTo(Gender.MALE);
        Assertions.assertThat(response.memberProfile().getJobId()).isEqualTo(2L);
        Assertions.assertThat(response.memberProfile().getAge()).isEqualTo(20);
        Assertions.assertThat(response.memberProfile().getHeight()).isEqualTo(180);
        Assertions.assertThat(response.memberProfile().getRegion()).isEqualTo(Region.DAEJEON);
        Assertions.assertThat(response.memberProfile().getReligionStatus()).isEqualTo(ReligionStatus.BUDDHIST);

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
        Long jobId = 2L;
        List<Long> hobbyIds = List.of(1L, 2L);
        MemberProfileUpdateRequest request = new MemberProfileUpdateRequest(
                "nickname", "MALE", 20, 180,
                jobId, null, "OTHER", "ENFJ",
                "DAILY_SMOKER", "QUITTING", "BUDDHIST", null
        );
        Member existingMember = Member.fromPhoneNumber("01012345678");

        // When
        Mockito.when(memberRepository.findById(memberId)).thenReturn(Optional.of(existingMember));
        Mockito.when(jobRepository.existsById(2L)).thenReturn(true);

        // Then
        MemberProfileUpdateResponse response = memberService.updateMember(memberId, request);
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.memberProfile().getNickname().getValue()).isEqualTo("nickname");
        Assertions.assertThat(response.memberProfile().getGender()).isEqualTo(Gender.MALE);
        Assertions.assertThat(response.memberProfile().getJobId()).isEqualTo(2L);
        Assertions.assertThat(response.memberProfile().getAge()).isEqualTo(20);
        Assertions.assertThat(response.memberProfile().getHeight()).isEqualTo(180);
        Assertions.assertThat(response.memberProfile().getRegion()).isNull();
        Assertions.assertThat(response.memberProfile().getReligionStatus()).isEqualTo(ReligionStatus.BUDDHIST);

        Assertions.assertThat(response.memberProfile().getMemberHobbyList()).hasSize(0);
    }
}
