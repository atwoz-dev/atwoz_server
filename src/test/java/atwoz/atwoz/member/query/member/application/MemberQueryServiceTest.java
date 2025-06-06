package atwoz.atwoz.member.query.member.application;

import atwoz.atwoz.community.command.domain.profileexchange.ProfileExchangeStatus;
import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.query.member.application.exception.ProfileAccessDeniedException;
import atwoz.atwoz.member.query.member.infra.MemberQueryRepository;
import atwoz.atwoz.member.query.member.view.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberQueryServiceTest {
    @Mock
    private MemberQueryRepository memberQueryRepository;

    @InjectMocks
    private MemberQueryService memberQueryService;

    @Nested
    @DisplayName("getInfoCache 메서드 테스트.")
    class getInfoCacheTest {

        @DisplayName("해당 결과가 존재하지 않을 경우, 예외 발생.")
        @Test
        void throwsExceptionWhenResultIsEmpty() {
            // Given
            Long memberId = 1L;
            when(memberQueryRepository.findInfoByMemberId(memberId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> memberQueryService.getInfoCache(memberId))
                .isInstanceOf(MemberNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getProfile 메서드 테스트.")
    class getProfileTest {
        @DisplayName("해당 결과가 존재하지 않을 경우, 예외 발생.")
        @Test
        void throwsExceptionWhenResultIsEmpty() {
            // Given
            Long memberId = 1L;
            when(memberQueryRepository.findProfileByMemberId(memberId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> memberQueryService.getProfile(memberId))
                .isInstanceOf(MemberNotFoundException.class);
        }
    }

    @DisplayName("getContact 메서드 테스트")
    @Nested
    class getContactTest {
        @DisplayName("해당 결과가 존재하지 않을 경우, 예외 발생.")
        @Test
        void throwsExceptionWhenResultIsEmpty() {
            // Given
            Long memberId = 1L;
            when(memberQueryRepository.findContactsByMemberId(memberId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> memberQueryService.getContact(memberId))
                .isInstanceOf(MemberNotFoundException.class);
        }
    }

    @DisplayName("getHeartBalance 메서드 테스트")
    @Nested
    class getHeartBalanceTest {
        @DisplayName("해당 결과가 존재하지 않을 경우, 예외 발생.")
        @Test
        void throwsExceptionWhenResultIsEmpty() {
            // Given
            Long memberId = 1L;
            when(memberQueryRepository.findHeartBalanceByMemberId(memberId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> memberQueryService.getHeartBalance(memberId))
                .isInstanceOf(MemberNotFoundException.class);
        }
    }

    @DisplayName("getMemberProfile 메서드 테스트")
    @Nested
    class getMemberProfileTest {

        Long memberId = 1L;
        Long otherMemberId = 2L;

        static Stream<Arguments> authorizedCaseWithDescription() {
            return Stream.of(
                Arguments.of(new ProfileAccessView(true, null, null, null, null, null, false), "이상형으로 소개받은 경우, 권한 존재."),
                Arguments.of(new ProfileAccessView(false, 2L, 1L, null, null, null, false), "매치 요청을 받은 경우, 권한 존재."),
                Arguments.of(
                    new ProfileAccessView(false, null, null, 2L, 1L, ProfileExchangeStatus.WAITING.name(), false),
                    "프로필 교환 요청을 받은 경우, 권한 존재."),
                Arguments.of(
                    new ProfileAccessView(false, null, null, 2L, 1L, ProfileExchangeStatus.APPROVE.name(), false),
                    "프로필 교환 요청을 수락 받은 경우, 권한 존재."),
                Arguments.of(new ProfileAccessView(false, null, null, null, null, null, true), "좋아요를 받은 경우, 권한 존재.")
            );
        }

        static Stream<Arguments> notAuthorizedCaseWithDescription() {
            return Stream.of(
                Arguments.of(new ProfileAccessView(false, 1L, 2L, null, null, null, false),
                    "매치 신청을 한 경우, 권한이 존재하지 않는다."),
                Arguments.of(
                    new ProfileAccessView(false, null, null, 1L, 2L, ProfileExchangeStatus.WAITING.name(), false),
                    "프로필 교환 요청이 대기중인 경우, 권한이 존재하지 않는다."),
                Arguments.of(
                    new ProfileAccessView(false, null, null, 1L, 2L, ProfileExchangeStatus.REJECTED.name(), false),
                    "프로필 교환 요청이 거절된 경우, 권한이 존재하지 않는다."),
                Arguments.of(new ProfileAccessView(false, null, null, null, null, null, false),
                    "어떠한 케이스도 존재하지 않으면, 권한이 존재하지 않는다.")
            );
        }

        @ParameterizedTest(name = "{1}")
        @MethodSource("notAuthorizedCaseWithDescription")
        @DisplayName("프로필 접근 권한이 존재하는 경우, 예외를 발생하지 않는다.")
        void throwsExceptionWhenProfileAccessIsNotAuthorized(ProfileAccessView profileAccessView, String predict) {
            // Given
            when(memberQueryRepository.findProfileAccessViewByMemberId(memberId, otherMemberId)).thenReturn(
                Optional.of(profileAccessView));

            // When & Then
            assertThatThrownBy(() -> memberQueryService.getMemberProfile(memberId, otherMemberId))
                .isInstanceOf(ProfileAccessDeniedException.class);
        }

        @ParameterizedTest(name = "{1}")
        @MethodSource("authorizedCaseWithDescription")
        @DisplayName("프로필 접근 권한이 존재하는 경우, 예외를 발생하지 않는다.")
        void notThrowsExceptionWhenProfileAccessIsAuthorized(ProfileAccessView profileAccessView, String predict) {
            // Given
            OtherMemberProfileView view = new OtherMemberProfileView(mock(BasicMemberInfo.class), mock(MatchInfo.class),
                mock(
                    ProfileExchangeInfo.class));
            when(memberQueryRepository.findProfileAccessViewByMemberId(memberId, otherMemberId)).thenReturn(
                Optional.of(profileAccessView));
            when(memberQueryRepository.findOtherProfileByMemberId(memberId, otherMemberId)).thenReturn(
                Optional.of(view));
            assertThatCode(
                () -> memberQueryService.getMemberProfile(memberId, otherMemberId)).doesNotThrowAnyException();
        }

        @DisplayName("프로필 접근 권한이 존재하지 않는 경우, 예외 발생.")
        @Test
        void throwsExceptionWhenProfileAccessIsNotAuthorized() {
            // Given
            ProfileAccessView profileAccessView = new ProfileAccessView(false, null, null, null, null, null, false);
            when(memberQueryRepository.findProfileAccessViewByMemberId(memberId, otherMemberId))
                .thenReturn(Optional.of(profileAccessView));

            // When & Then
            assertThatThrownBy(() -> memberQueryService.getMemberProfile(memberId, otherMemberId))
                .isInstanceOf(ProfileAccessDeniedException.class);
        }

        @DisplayName("프로필 교환 요청을 신청한 뒤, 대기중인 경우, 예외 발생.")
        @Test
        void throwsExceptionWhenProfileIsNotAuthorized() {
            // Given
            ProfileAccessView profileAccessView = new ProfileAccessView(false, memberId, otherMemberId, memberId,
                otherMemberId, ProfileExchangeStatus.WAITING.name(), false);
            when(memberQueryRepository.findProfileAccessViewByMemberId(memberId, otherMemberId))
                .thenReturn(Optional.of(profileAccessView));

            // When & Then
            assertThatThrownBy(() -> memberQueryService.getMemberProfile(memberId, otherMemberId))
                .isInstanceOf(ProfileAccessDeniedException.class);
        }
    }
}
