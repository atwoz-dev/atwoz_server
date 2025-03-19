package atwoz.atwoz.member.command.application.member;

import atwoz.atwoz.auth.domain.TokenRepository;
import atwoz.atwoz.auth.infra.JwtProvider;
import atwoz.atwoz.common.enums.Role;
import atwoz.atwoz.member.command.application.member.dto.MemberLoginServiceDto;
import atwoz.atwoz.member.command.application.member.exception.BannedMemberException;
import atwoz.atwoz.member.command.application.member.exception.MemberLoginConflictException;
import atwoz.atwoz.member.command.domain.introduction.MemberIdeal;
import atwoz.atwoz.member.command.domain.introduction.MemberIdealCommandRepository;
import atwoz.atwoz.member.command.domain.member.ActivityStatus;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MemberAuthServiceTest {
    private Member permanentStoppedMember;
    private Member member;
    private Long memberId;

    @Mock
    private MemberCommandRepository memberCommandRepository;

    @Mock
    private MemberIdealCommandRepository memberIdealCommandRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private MemberAuthService memberAuthService;

    @BeforeEach
    void setUp() {
        member = Member.fromPhoneNumber("01012345678");
        memberId = 1L;
        ReflectionTestUtils.setField(member, "id", memberId);
        ReflectionTestUtils.setField(member, "activityStatus", ActivityStatus.ACTIVE);

        permanentStoppedMember = Member.fromPhoneNumber("01012345678");
        ReflectionTestUtils.setField(permanentStoppedMember, "id", 2L);
        ReflectionTestUtils.setField(permanentStoppedMember, "activityStatus", ActivityStatus.BANNED);

    }

    @Test
    @DisplayName("영구 정지된 사용자가 로그인할 경우 예외 처리")
    void shouldThrowExceptionWhenLoginAttemptedByPermanentStoppedMember() {
        // Given
        String phoneNumber = "01012345678";

        Mockito.when(memberCommandRepository.findByPhoneNumber(phoneNumber))
                .thenReturn(Optional.of(permanentStoppedMember));

        // When & Then
        Assertions.assertThatThrownBy(() -> memberAuthService.login(phoneNumber)).isInstanceOf(BannedMemberException.class);
    }

    @Test
    @DisplayName("기존 유저가 로그인할 경우, 토큰 발급")
    void shouldCreateTokenWhenUserIsRegistered() {
        String phoneNumber = "01012345678";
        Instant fixedInstant = Instant.parse("2024-01-01T00:00:00Z");

        try (MockedStatic<Instant> mockedInstant = Mockito.mockStatic(Instant.class)) {
            mockedInstant.when(Instant::now).thenReturn(fixedInstant);

            Mockito.when(memberCommandRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(member));
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

        try (MockedStatic<Instant> mockedInstant = Mockito.mockStatic(Instant.class);
             MockedStatic<MemberIdeal> mockedMemberIdeal = Mockito.mockStatic(MemberIdeal.class);
        ) {
            mockedInstant.when(Instant::now).thenReturn(fixedInstant);

            Mockito.when(memberCommandRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());
            Mockito.when(jwtProvider.createAccessToken(Mockito.anyLong(), Mockito.eq(Role.MEMBER), Mockito.eq(fixedInstant)))
                    .thenReturn("accessToken");
            Mockito.when(memberCommandRepository.save(Mockito.any())).thenReturn(member);

            MemberIdeal memberIdeal = mock(MemberIdeal.class);
            mockedMemberIdeal.when(() -> MemberIdeal.from(memberId)).thenReturn(memberIdeal);
            Mockito.when(memberIdealCommandRepository.save(memberIdeal)).thenReturn(memberIdeal);

            // When
            MemberLoginServiceDto response = memberAuthService.login(phoneNumber);

            // Then
            Assertions.assertThat(response.accessToken()).isNotNull();
            Assertions.assertThat(response.accessToken()).isEqualTo("accessToken");
            Assertions.assertThat(response.isProfileSettingNeeded()).isTrue();

            verify(memberIdealCommandRepository).save(memberIdeal);
        }
    }

    @Test
    @DisplayName("동시 로그인으로 인해, 유니크 제약조건에 걸린 경우 예외 반환")
    void shouldThrowExceptionWhenUniqueConstraint() {
        // Given
        String phoneNumber = "01012345678";

        // When
        Mockito.when(memberCommandRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());
        Mockito.when(memberCommandRepository.save(Mockito.any())).thenThrow(DataIntegrityViolationException.class);

        // When & Then
        Assertions.assertThatThrownBy(() -> memberAuthService.login(phoneNumber)).isInstanceOf(MemberLoginConflictException.class);
    }
}
