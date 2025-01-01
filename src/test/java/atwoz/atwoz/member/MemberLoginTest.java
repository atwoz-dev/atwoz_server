package atwoz.atwoz.member;


import atwoz.atwoz.common.auth.context.Role;
import atwoz.atwoz.common.auth.jwt.JwtProvider;
import atwoz.atwoz.member.application.MemberAuthService;
import atwoz.atwoz.member.application.dto.MemberLoginServiceDto;
import atwoz.atwoz.member.domain.member.ActivityStatus;
import atwoz.atwoz.member.domain.member.Member;
import atwoz.atwoz.member.domain.member.MemberRepository;
import atwoz.atwoz.member.exception.MemberPermanentStopException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MemberLoginTest {

    private Member permanentStoppedMember;
    private Member member;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private MemberAuthService memberAuthService;

    @BeforeEach
    void setUp() {
        member = Member.createFromPhoneNumber("01012345678");
        ReflectionTestUtils.setField(member, "id", 1L);
        ReflectionTestUtils.setField(member, "activityStatus", ActivityStatus.ACTIVE);

        permanentStoppedMember = Member.createFromPhoneNumber("01012345678");
        ReflectionTestUtils.setField(permanentStoppedMember, "id", 2L);
        ReflectionTestUtils.setField(permanentStoppedMember, "activityStatus", ActivityStatus.PERMANENT_STOP);

    }

    @Test
    @DisplayName("영구 정지된 사용자가 로그인할 경우 예외 처리")
    void shouldThrowExceptionWhenLoginAttemptedByPermanentStoppedMember() {
        // Given
        String phoneNumber = "01012345678";

        Mockito.when(memberRepository.findByPhoneNumber(phoneNumber))
                .thenReturn(Optional.of(permanentStoppedMember));

        // When & Then
        Assertions.assertThatThrownBy(() -> memberAuthService.login(phoneNumber)).isInstanceOf(MemberPermanentStopException.class);
    }

    @Test
    @DisplayName("기존 유저가 로그인할 경우, 토큰 발급")
    void shouldCreateTokenWhenUserIsRegistered() {
        String phoneNumber = "01012345678";
        Instant fixedInstant = Instant.parse("2024-01-01T00:00:00Z");

        try (MockedStatic<Instant> mockedInstant = Mockito.mockStatic(Instant.class)) {
            mockedInstant.when(Instant::now).thenReturn(fixedInstant);

            Mockito.when(memberRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(member));
            Mockito.when(jwtProvider.createAccessToken(Mockito.anyLong(), Mockito.eq(Role.MEMBER), Mockito.eq(fixedInstant)))
                    .thenReturn("accessToken");

            // When
            String token = memberAuthService.login(phoneNumber).accessToken();

            // Then
            Assertions.assertThat(token).isNotNull();
            Assertions.assertThat(token).isEqualTo("accessToken");
        }
    }

    @Test
    @DisplayName("등록되지 않은 유저가 로그인할 경우, 새롭게 생성하여 토큰 발급")
    void shouldCreateNewMemberAndTokenWhenUserIsNotRegistered() {
        // Given
        String phoneNumber = "01012345678";
        Instant fixedInstant = Instant.parse("2024-01-01T00:00:00Z");

        try (MockedStatic<Instant> mockedInstant = Mockito.mockStatic(Instant.class)) {
            mockedInstant.when(Instant::now).thenReturn(fixedInstant);

            Mockito.when(memberRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());
            Mockito.when(memberRepository.save(Mockito.any(Member.class))).thenReturn(member);
            Mockito.when(jwtProvider.createAccessToken(Mockito.anyLong(), Mockito.eq(Role.MEMBER), Mockito.eq(fixedInstant)))
                    .thenReturn("accessToken");

            // When
            MemberLoginServiceDto response = memberAuthService.login(phoneNumber);

            // Then
            Assertions.assertThat(response.accessToken()).isNotNull();
            Assertions.assertThat(response.accessToken()).isEqualTo("accessToken");
            Assertions.assertThat(response.isProfileSettingNeeded()).isTrue();
        }
    }
}
