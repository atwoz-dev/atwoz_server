package atwoz.atwoz.member.command.application.member;

import atwoz.atwoz.auth.domain.TokenParser;
import atwoz.atwoz.auth.domain.TokenRepository;
import atwoz.atwoz.auth.infra.JwtProvider;
import atwoz.atwoz.common.enums.Role;
import atwoz.atwoz.member.command.application.member.dto.MemberLoginServiceDto;
import atwoz.atwoz.member.command.application.member.exception.MemberDeletedException;
import atwoz.atwoz.member.command.application.member.exception.MemberLoginConflictException;
import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.command.application.member.exception.PermanentlySuspendedMemberException;
import atwoz.atwoz.member.command.application.member.sms.AuthMessageService;
import atwoz.atwoz.member.command.domain.member.ActivityStatus;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

@ExtendWith(MockitoExtension.class)
class MemberAuthServiceTest {

    @Mock
    private MemberCommandRepository memberCommandRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private TokenParser tokenParser;

    @Mock
    private AuthMessageService authMessageService;

    @InjectMocks
    private MemberAuthService memberAuthService;


    @Nested
    @DisplayName("로그인 및 회원가입 테스트")
    class Login {
        private Member permanentStoppedMember;
        private Member member;
        private Long memberId;

        @BeforeEach
        void setUp() {
            member = Member.fromPhoneNumber("01012345678");
            memberId = 1L;
            ReflectionTestUtils.setField(member, "id", memberId);
            ReflectionTestUtils.setField(member, "activityStatus", ActivityStatus.ACTIVE);

            permanentStoppedMember = Member.fromPhoneNumber("01012345678");
            ReflectionTestUtils.setField(permanentStoppedMember, "id", 2L);
            ReflectionTestUtils.setField(permanentStoppedMember, "activityStatus",
                ActivityStatus.SUSPENDED_PERMANENTLY);

        }

        @Test
        @DisplayName("영구 정지된 사용자가 로그인할 경우 예외 처리")
        void shouldThrowExceptionWhenLoginAttemptedByPermanentStoppedMember() {
            // Given
            String phoneNumber = "01012345678";
            String code = "01012345678";

            Mockito.when(memberCommandRepository.findByPhoneNumber(phoneNumber))
                .thenReturn(Optional.of(permanentStoppedMember));
            Mockito.doNothing().when(authMessageService).authenticate(phoneNumber, code);

            // When & Then
            Assertions.assertThatThrownBy(() -> memberAuthService.login(phoneNumber, code))
                .isInstanceOf(PermanentlySuspendedMemberException.class);
        }

        @Test
        @DisplayName("회원 탈퇴한 사용자가 로그인할 경우 예외 처리")
        void shouldThrowExceptionWhenLoginAttemptedByDeletedMember() {
            // Given
            String phoneNumber = "01012345678";
            String code = "01012345678";
            Member deletedMember = Member.fromPhoneNumber("01012345678");
            deletedMember.delete();

            Mockito.when(memberCommandRepository.findByPhoneNumber(phoneNumber))
                .thenReturn(Optional.of(deletedMember));
            Mockito.doNothing().when(authMessageService).authenticate(phoneNumber, code);

            // When & Then
            Assertions.assertThatThrownBy(() -> memberAuthService.login(phoneNumber, code))
                .isInstanceOf(MemberDeletedException.class);
        }

        @Test
        @DisplayName("기존 유저가 로그인할 경우, 토큰 발급")
        void shouldCreateTokenWhenUserIsRegistered() {
            String phoneNumber = "01012345678";
            String code = "01012345678";
            Instant fixedInstant = Instant.parse("2024-01-01T00:00:00Z");

            try (MockedStatic<Instant> mockedInstant = Mockito.mockStatic(Instant.class)) {
                mockedInstant.when(Instant::now).thenReturn(fixedInstant);

                Mockito.when(memberCommandRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(member));
                Mockito.when(
                        jwtProvider.createAccessToken(Mockito.anyLong(), Mockito.eq(Role.MEMBER), Mockito.eq(fixedInstant)))
                    .thenReturn("accessToken");
                Mockito.doNothing().when(authMessageService).authenticate(phoneNumber, code);

                // When
                String token = memberAuthService.login(phoneNumber, code).accessToken();

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
            String code = "01012345678";
            Instant fixedInstant = Instant.parse("2024-01-01T00:00:00Z");

            try (MockedStatic<Instant> mockedInstant = Mockito.mockStatic(Instant.class)) {
                mockedInstant.when(Instant::now).thenReturn(fixedInstant);

                Mockito.when(memberCommandRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());
                Mockito.when(
                        jwtProvider.createAccessToken(Mockito.anyLong(), Mockito.eq(Role.MEMBER), Mockito.eq(fixedInstant)))
                    .thenReturn("accessToken");
                Mockito.when(memberCommandRepository.save(Mockito.any())).thenReturn(member);
                Mockito.doNothing().when(authMessageService).authenticate(phoneNumber, code);

                // When
                MemberLoginServiceDto response = memberAuthService.login(phoneNumber, code);

                // Then
                Assertions.assertThat(response.accessToken()).isNotNull();
                Assertions.assertThat(response.accessToken()).isEqualTo("accessToken");
                Assertions.assertThat(response.isProfileSettingNeeded()).isTrue();
            }
        }

        @Test
        @DisplayName("동시 로그인으로 인해, 유니크 제약조건에 걸린 경우 예외 반환")
        void shouldThrowExceptionWhenUniqueConstraint() {
            // Given
            String phoneNumber = "01012345678";
            String code = "01012345678";

            // When
            Mockito.when(memberCommandRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());
            Mockito.when(memberCommandRepository.save(Mockito.any())).thenThrow(DataIntegrityViolationException.class);
            Mockito.doNothing().when(authMessageService).authenticate(phoneNumber, code);

            // When & Then
            Assertions.assertThatThrownBy(() -> memberAuthService.login(phoneNumber, code))
                .isInstanceOf(MemberLoginConflictException.class);
        }
    }

    @Nested
    @DisplayName("회원 탈퇴 테스트.")
    class Delete {
        @DisplayName("회원으로 존재하지 않는 경우, 예외 발생.")
        @Test
        void throwExceptionWhenMemberIsNotFound() {
            // Given
            Long memberId = 1L;
            String refreshToken = "refreshToken";
            Mockito.when(memberCommandRepository.findById(memberId)).thenReturn(Optional.empty());

            // When & Then
            Assertions.assertThatThrownBy(() -> memberAuthService.delete(memberId, refreshToken))
                .isInstanceOf(MemberNotFoundException.class);
        }

        @DisplayName("존재하는 경우, 회원을 SoftDelete 한다.")
        @Test
        void softDeleteWhenMemberIsFound() {
            // Given
            Long memberId = 1L;
            String refreshToken = "refreshToken";
            Member member = Member.fromPhoneNumber("01012345678");
            Mockito.when(memberCommandRepository.findById(memberId)).thenReturn(Optional.of(member));

            // When
            memberAuthService.delete(memberId, refreshToken);

            // Then
            Assertions.assertThat(member.isDeleted()).isTrue();
        }
    }


}
