package atwoz.atwoz.member.application;

import atwoz.atwoz.hobby.domain.Hobby;
import atwoz.atwoz.hobby.domain.HobbyRepository;
import atwoz.atwoz.job.domain.Job;
import atwoz.atwoz.job.domain.JobRepository;
import atwoz.atwoz.job.exception.JobNotFoundException;
import atwoz.atwoz.member.application.dto.MemberProfileResponse;
import atwoz.atwoz.member.application.dto.MemberProfileUpdateRequest;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberProfileTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private HobbyRepository hobbyRepository;

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private MemberProfileService memberProfileService;

    @DisplayName("멤버를 찾을 수 없을 경우, 예외 발생")
    @Test
    void throwExceptionWhenMemberIsNotFound() {
        // Given
        Long memberId = 1L;

        // When
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> memberProfileService.updateMember(memberId, null)).isInstanceOf(MemberNotFoundException.class);
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
        assertThatThrownBy(() -> memberProfileService.updateMember(memberId, invalidRequest))
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
        assertThatThrownBy(() -> memberProfileService.updateMember(memberId, invalidRequest))
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
        assertThatThrownBy(() -> memberProfileService.updateMember(memberId, invalidRequest))
                .isInstanceOf(InvalidHobbyIdException.class);
    }

    @DisplayName("멤버가 존재하는 경우, 성공")
    @Test
    void succeedsWhenMemberIsFound() {
        /**
         * TODO : 수정.
         */
        // Given
        Long memberId = 1L;
        Long jobId = 2L;
        Job job = Job.from("직업");
        Set<Long> hobbyIds = Set.of(1L, 2L);
        List<Hobby> hobbies = List.of(Hobby.from("취미1"), Hobby.from("취미2"));

        MemberProfileUpdateRequest request = new MemberProfileUpdateRequest(
                "nickname", "MALE", 20, 180,
                jobId, "Daejeon", "OTHER", "ENFJ",
                "DAILY", "QUIT", "BUDDHIST", hobbyIds
        );
        Member existingMember = Member.fromPhoneNumber("01012345678");

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(existingMember));
        when(hobbyRepository.countHobbiesByIdIn(hobbyIds)).thenReturn(2L);
        when(jobRepository.existsById(jobId)).thenReturn(true);
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(hobbyRepository.findHobbiesByIdIn(hobbyIds)).thenReturn(hobbies);
        // When
        MemberProfileResponse response = memberProfileService.updateMember(memberId, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.nickname()).isEqualTo("nickname");
        assertThat(response.gender()).isEqualTo(Gender.MALE.toString());
        assertThat(response.job()).isEqualTo(job.getName());
        assertThat(response.hobbies().size()).isEqualTo(hobbies.size());
        assertThat(response.height()).isEqualTo(180);
        assertThat(response.region()).isEqualTo(Region.DAEJEON.toString());
        assertThat(response.religion()).isEqualTo(Religion.BUDDHIST.toString());
    }

    @DisplayName("멤버가 존재하는 경우, 특정 값에 null 이 포함되어 있더라도 성공.")
    @Test
    void succeedsWhenNullValueExists() {
        // Given
        Long memberId = 1L;
        Long jobId = 2L;
        Job job = Job.from("직업");

        MemberProfileUpdateRequest request = new MemberProfileUpdateRequest(
                "nickname", "MALE", 20, 180,
                jobId, null, "OTHER", "ENFJ",
                "DAILY", "QUIT", "BUDDHIST", null
        );
        Member existingMember = Member.fromPhoneNumber("01012345678");

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(existingMember));
        when(jobRepository.existsById(2L)).thenReturn(true);
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        // When
        MemberProfileResponse response = memberProfileService.updateMember(memberId, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.nickname()).isEqualTo("nickname");
        assertThat(response.gender()).isEqualTo(Gender.MALE.toString());
        assertThat(response.job()).isEqualTo(job.getName());
        assertThat(response.hobbies().size()).isEqualTo(0);
        assertThat(response.height()).isEqualTo(180);
        assertThat(response.region()).isNull();
        assertThat(response.religion()).isEqualTo(Religion.BUDDHIST.toString());
    }
}
