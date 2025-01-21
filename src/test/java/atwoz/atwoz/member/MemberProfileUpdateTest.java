package atwoz.atwoz.member;

import atwoz.atwoz.hobby.domain.HobbyRepository;
import atwoz.atwoz.job.domain.JobRepository;
import atwoz.atwoz.job.exception.JobNotFoundException;
import atwoz.atwoz.member.application.MemberService;
import atwoz.atwoz.member.application.dto.MemberProfileResponse;
import atwoz.atwoz.member.application.dto.MemberProfileUpdateRequest;
import atwoz.atwoz.member.application.dto.MemberProfileUpdateResponse;
import atwoz.atwoz.member.application.exception.MemberNotFoundException;
import atwoz.atwoz.member.domain.member.*;
import atwoz.atwoz.member.domain.member.exception.InvalidHobbyIdException;
import atwoz.atwoz.member.domain.member.exception.InvalidMemberEnumValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberProfileUpdateTest {

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
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> memberService.updateMember(memberId, null)).isInstanceOf(MemberNotFoundException.class);
    }

    @DisplayName("멤버가 존재하지만 Enum 값이 적절하지 않은 경우, 예외 발생")
    @Test
    void throwExceptionWhenEnumValueIsInvalid() {
        // Given
        Long memberId = 1L;
        Long jobId = 2L;
        Set<Long> hobbyIds = Set.of(1L, 2L);

        MemberProfileUpdateRequest invalidRequest = new MemberProfileUpdateRequest(
                "nickname", "INVALID_ENUM", 20, 180, // 잘못된 gender 값
                jobId, "Daejeon", "OTHER", "ENFJ",
                "DAILY", "QUIT", "BUDDHIST", hobbyIds
        );
        Member existingMember = Member.fromPhoneNumber("01012345678");

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(existingMember));
        when(jobRepository.existsById(jobId)).thenReturn(true);
        when(hobbyRepository.countHobbiesByIdIn(hobbyIds)).thenReturn(2L);

        // When & Then
        assertThatThrownBy(() -> memberService.updateMember(memberId, invalidRequest))
                .isInstanceOf(InvalidMemberEnumValueException.class);
    }

    @DisplayName("멤버가 존재하지만 유효하지 않은 직업 ID를 가질 경우, 예외 발생")
    @Test
    void throwExceptionWhenJobIdIsInvalid() {
        // Given
        Long memberId = 1L;
        Long jobId = 2L;
        Set<Long> hobbyIds = Set.of(1L, 2L);

        MemberProfileUpdateRequest invalidRequest = new MemberProfileUpdateRequest(
                "nickname", "INVALID_ENUM", 20, 180, // 잘못된 gender 값
                jobId, "Daejeon", "OTHER", "ENFJ",
                "DAILY", "QUIT", "BUDDHIST", hobbyIds
        );
        Member existingMember = Member.fromPhoneNumber("01012345678");

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(existingMember));
        when(jobRepository.existsById(jobId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> memberService.updateMember(memberId, invalidRequest))
                .isInstanceOf(JobNotFoundException.class);
    }

    @DisplayName("멤버가 존재하지만 유효하지 않은 취미 ID를 가질 경우, 예외 발생")
    @Test
    void throwExceptionWhenHobbyIdIsInvalid() {
        // Given
        Long memberId = 1L;
        Long jobId = 2L;
        Set<Long> hobbyIds = Set.of(1L, 2L);

        MemberProfileUpdateRequest invalidRequest = new MemberProfileUpdateRequest(
                "nickname", "INVALID_ENUM", 20, 180, // 잘못된 gender 값
                jobId, "Daejeon", "OTHER", "ENFJ",
                "DAILY", "QUIT", "BUDDHIST", hobbyIds
        );
        Member existingMember = Member.fromPhoneNumber("01012345678");

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(existingMember));
        when(jobRepository.existsById(jobId)).thenReturn(true);
        when(hobbyRepository.countHobbiesByIdIn(hobbyIds)).thenReturn(1L);

        // When & Then
        assertThatThrownBy(() -> memberService.updateMember(memberId, invalidRequest))
                .isInstanceOf(InvalidHobbyIdException.class);
    }

    @DisplayName("멤버가 존재하는 경우, 성공")
    @Test
    void succeedsWhenMemberIsFound() {
        // Given
        Long memberId = 1L;
        Long jobId = 2L;
        Set<Long> hobbyIds = Set.of(1L, 2L);

        MemberProfileUpdateRequest request = new MemberProfileUpdateRequest(
                "nickname", "MALE", 20, 180,
                jobId, "Daejeon", "OTHER", "ENFJ",
                "DAILY", "QUIT", "BUDDHIST", hobbyIds
        );
        Member existingMember = Member.fromPhoneNumber("01012345678");

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(existingMember));
        when(hobbyRepository.countHobbiesByIdIn(hobbyIds)).thenReturn(2L);
        when(jobRepository.existsById(jobId)).thenReturn(true);

        // When
        MemberProfileResponse response = memberService.updateMember(memberId, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.memberProfile().getNickname().getValue()).isEqualTo("nickname");
        assertThat(response.memberProfile().getGender()).isEqualTo(Gender.MALE);
        assertThat(response.memberProfile().getJobId()).isEqualTo(2L);
        assertThat(response.memberProfile().getAge()).isEqualTo(20);
        assertThat(response.memberProfile().getHeight()).isEqualTo(180);
        assertThat(response.memberProfile().getRegion()).isEqualTo(Region.DAEJEON);
        assertThat(response.memberProfile().getReligion()).isEqualTo(Religion.BUDDHIST);

        assertThat(response.memberProfile().getHobbyIds()).hasSize(2);
    }

    @DisplayName("멤버가 존재하는 경우, 특정 값에 null 이 포함되어 있더라도 성공.")
    @Test
    void succeedsWhenNullValueExists() {
        // Given
        Long memberId = 1L;
        Long jobId = 2L;
        MemberProfileUpdateRequest request = new MemberProfileUpdateRequest(
                "nickname", "MALE", 20, 180,
                jobId, null, "OTHER", "ENFJ",
                "DAILY", "QUIT", "BUDDHIST", null
        );
        Member existingMember = Member.fromPhoneNumber("01012345678");

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(existingMember));
        when(jobRepository.existsById(2L)).thenReturn(true);

        // When
        MemberProfileResponse response = memberService.updateMember(memberId, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.memberProfile().getNickname().getValue()).isEqualTo("nickname");
        assertThat(response.memberProfile().getGender()).isEqualTo(Gender.MALE);
        assertThat(response.memberProfile().getJobId()).isEqualTo(2L);
        assertThat(response.memberProfile().getAge()).isEqualTo(20);
        assertThat(response.memberProfile().getHeight()).isEqualTo(180);
        assertThat(response.memberProfile().getRegion()).isNull();
        assertThat(response.memberProfile().getReligion()).isEqualTo(Religion.BUDDHIST);
        assertThat(response.memberProfile().getHobbyIds()).isNull();
    }
}
