package atwoz.atwoz.community.command.application.profileexchange;

import atwoz.atwoz.common.MockEventsExtension;
import atwoz.atwoz.common.repository.LockRepository;
import atwoz.atwoz.community.command.application.profileexchange.exception.ProfileExchangeAlreadyExistsException;
import atwoz.atwoz.community.command.application.profileexchange.exception.ProfileExchangeNotFoundException;
import atwoz.atwoz.community.command.application.profileexchange.exception.ProfileExchangeResponderMismatchException;
import atwoz.atwoz.community.command.domain.profileexchange.ProfileExchange;
import atwoz.atwoz.community.command.domain.profileexchange.ProfileExchangeRepository;
import atwoz.atwoz.community.command.domain.profileexchange.ProfileExchangeStatus;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import atwoz.atwoz.member.command.domain.member.vo.MemberProfile;
import atwoz.atwoz.member.command.domain.member.vo.Nickname;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith({MockitoExtension.class, MockEventsExtension.class})
class ProfileExchangeServiceTest {

    Member sender = Member.fromPhoneNumber("01012345678");
    @Mock
    private MemberCommandRepository memberCommandRepository;

    @Mock
    private ProfileExchangeRepository profileExchangeRepository;

    @Mock
    private LockRepository lockRepository;

    @InjectMocks
    private ProfileExchangeService profileExchangeService;

    @BeforeEach
    void setUp() {
        MemberProfile memberProfile = MemberProfile.builder()
            .nickname(Nickname.from("닉네임"))
            .build();
        setField(sender, "id", 1L);

        sender.updateProfile(memberProfile);
    }

    @Nested
    @DisplayName("프로필 교환 요청")
    class Request {

        @Test
        @DisplayName("프로필 교환이 이미 존재할 경우, 예외 발생")
        void throwsExceptionWhenProfileExchangeExistsBetween() {
            // Given
            long requesterId = 1L;
            long responderId = 2L;

            Mockito.when(profileExchangeRepository.existsProfileExchangeBetween(requesterId, responderId))
                .thenReturn(true);

            Mockito.doAnswer(invocation -> {
                Runnable runnable = invocation.getArgument(1);
                runnable.run();
                return null;
            }).when(lockRepository).withNamedLock(any(), any());

            // When & Then
            assertThatThrownBy(() -> profileExchangeService.request(requesterId, responderId))
                .isInstanceOf(ProfileExchangeAlreadyExistsException.class);
        }

        @DisplayName("프로필 교환이 존재하지 않으면, 프로필 교환을 생성한다.")
        @Test
        void request() {
            // Given
            long requesterId = 1L;
            long responderId = 2L;

            Mockito.when(profileExchangeRepository.existsProfileExchangeBetween(requesterId, responderId))
                .thenReturn(false);
            Mockito.when(memberCommandRepository.findById(requesterId))
                .thenReturn(Optional.of(sender));

            Mockito.doAnswer(invocation -> {
                Runnable runnable = invocation.getArgument(1);
                runnable.run();
                return null;
            }).when(lockRepository).withNamedLock(any(), any());

            // When
            profileExchangeService.request(requesterId, responderId);

            // Then
            Mockito.verify(profileExchangeRepository).save(Mockito.any(ProfileExchange.class));
        }
    }

    @Nested
    @DisplayName("프로필 교환 응답")
    class Response {

        @DisplayName("프로필 교환이 존재하지 않는 경우, 예외 발생.")
        @Test
        void throwsExceptionWhenProfileExchangeNotExists() {
            // Given
            long profileExchangeId = 1L;
            long responderId = 2L;

            Mockito.when(profileExchangeRepository.findById(profileExchangeId))
                .thenReturn(Optional.empty());

            Mockito.when(memberCommandRepository.findById(responderId))
                .thenReturn(Optional.of(sender));

            // When & Then
            assertThatThrownBy(() -> profileExchangeService.approve(profileExchangeId, responderId))
                .isInstanceOf(ProfileExchangeNotFoundException.class);
            assertThatThrownBy(() -> profileExchangeService.reject(profileExchangeId, responderId))
                .isInstanceOf(ProfileExchangeNotFoundException.class);
        }

        @DisplayName("프로필 교환의 응답자와 파라미터 응답자의 아이디가 서로 다른 경우, 예외 발생.")
        @Test
        void throwsExceptionWhenResponderIdNotEqualResponderIdInProfileExchange() {
            // Given
            long profileExchangeId = 1L;
            long requesterId = 2L;
            long responderId = 3L;
            long anotherResponderId = 4L;

            ProfileExchange profileExchange = ProfileExchange.request(requesterId, responderId,
                sender.getProfile().getNickname().getValue());

            Mockito.when(profileExchangeRepository.findById(profileExchangeId))
                .thenReturn(Optional.of(profileExchange));
            Mockito.when(memberCommandRepository.findById(anotherResponderId))
                .thenReturn(Optional.of(sender));

            // When & Then
            assertThatThrownBy(() -> profileExchangeService.approve(profileExchangeId, anotherResponderId))
                .isInstanceOf(ProfileExchangeResponderMismatchException.class);
            assertThatThrownBy(() -> profileExchangeService.reject(profileExchangeId, anotherResponderId))
                .isInstanceOf(ProfileExchangeResponderMismatchException.class);
        }

        @DisplayName("프로필 교환 신청을 수락한다.")
        @Test
        void approve() {
            // Given
            long profileExchangeId = 1L;
            long requesterId = 2L;
            long responderId = 3L;
            ProfileExchange profileExchange = ProfileExchange.request(requesterId, responderId,
                sender.getProfile().getNickname().getValue());

            Mockito.when(profileExchangeRepository.findById(profileExchangeId))
                .thenReturn(Optional.of(profileExchange));
            Mockito.when(memberCommandRepository.findById(responderId))
                .thenReturn(Optional.of(sender));

            // When
            profileExchangeService.approve(profileExchangeId, responderId);

            // Then
            assertThat(profileExchange.getStatus()).isEqualTo(ProfileExchangeStatus.APPROVE);
        }

        @DisplayName("프로필 교환 신청을 거절한다.")
        @Test
        void reject() {
            // Given
            long profileExchangeId = 1L;
            long requesterId = 2L;
            long responderId = 3L;
            ProfileExchange profileExchange = ProfileExchange.request(requesterId, responderId,
                sender.getProfile().getNickname().getValue());

            Mockito.when(profileExchangeRepository.findById(profileExchangeId))
                .thenReturn(Optional.of(profileExchange));
            Mockito.when(memberCommandRepository.findById(responderId))
                .thenReturn(Optional.of(sender));

            // When
            profileExchangeService.reject(profileExchangeId, responderId);

            // Then
            assertThat(profileExchange.getStatus()).isEqualTo(ProfileExchangeStatus.REJECTED);
        }
    }
}
