package atwoz.atwoz.member;

import atwoz.atwoz.hobby.domain.HobbyRepository;
import atwoz.atwoz.job.domain.JobRepository;
import atwoz.atwoz.member.application.MemberService;
import atwoz.atwoz.member.application.dto.MemberContactResponse;
import atwoz.atwoz.member.application.exception.MemberNotFoundException;
import atwoz.atwoz.member.domain.member.KakaoId;
import atwoz.atwoz.member.domain.member.Member;
import atwoz.atwoz.member.domain.member.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MemberContactGetTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private HobbyRepository hobbyRepository;

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("존재하지 않은 아이디의 경우 연락처 조회 실패.")
    void isFailWhenMemberIsNotExists() {
        // Given
        String phoneNumber = "01012345678";
        Member member = Member.fromPhoneNumber(phoneNumber);

        Mockito.when(memberRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        // When
        Assertions.assertThatThrownBy(() -> memberService.getContactAll(member.getId()))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("아이디가 존재하는 경우 연락처 조회 성공.")
    void isSuccessWhenMemberIsExists() {
        // Given
        String phoneNumber = "01012345678";
        String kakaoId = "kakaoId";
        Member member = Member.fromPhoneNumber(phoneNumber);
        member.updateContactByKakaoId(KakaoId.from(kakaoId));

        Mockito.when(memberRepository.findById(Mockito.any())).thenReturn(Optional.of(member));

        // When
        MemberContactResponse memberContactResponse = memberService.getContactAll(member.getId());

        // Then
        Assertions.assertThat(memberContactResponse).isNotNull();
        Assertions.assertThat(memberContactResponse.phoneNumber()).isEqualTo(phoneNumber);
        Assertions.assertThat(memberContactResponse.kakaoId()).isEqualTo(kakaoId);
    }
}
