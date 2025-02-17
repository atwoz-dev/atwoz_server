package atwoz.atwoz.admin.command.application.screening;

import atwoz.atwoz.admin.command.domain.screening.RejectionReasonType;
import atwoz.atwoz.admin.command.domain.screening.Screening;
import atwoz.atwoz.admin.command.domain.screening.ScreeningCommandRepository;
import atwoz.atwoz.admin.command.domain.screening.ScreeningNotFoundException;
import atwoz.atwoz.admin.presentation.screening.dto.ScreeningApproveRequest;
import atwoz.atwoz.admin.presentation.screening.dto.ScreeningRejectRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScreeningServiceTest {

    @Mock
    private ScreeningCommandRepository screeningCommandRepository;

    @InjectMocks
    private ScreeningService screeningService;

    @Nested
    @DisplayName("create() 테스트")
    class Create {

        @Test
        @DisplayName("멤버 심사가 아직 존재하지 않는다면 MemberScreening을 생성하고 저장합니다.")
        void createNewMemberScreeningWhenNotExists() {
            // given
            Long memberId = 100L;
            when(screeningCommandRepository.existsByMemberId(memberId)).thenReturn(false);

            // when
            screeningService.create(memberId);

            // then
            verify(screeningCommandRepository).existsByMemberId(memberId);
            verify(screeningCommandRepository).save(any(Screening.class));
        }

        @Test
        @DisplayName("이미 동일 멤버의 MemberScreening이 존재한다면 저장하지 않고 경고 로그를 남기고 종료합니다.")
        void skipCreationWhenAlreadyExists() {
            // given
            Long memberId = 200L;
            when(screeningCommandRepository.existsByMemberId(memberId)).thenReturn(true);

            // when
            screeningService.create(memberId);

            // then
            verify(screeningCommandRepository).existsByMemberId(memberId);
            verify(screeningCommandRepository, never()).save(any(Screening.class));
        }
    }

    @Nested
    @DisplayName("approve() 테스트")
    class Approve {

        @Test
        @DisplayName("존재하는 MemberScreening이면 approve(adminId)를 호출합니다.")
        void approveWhenScreeningExists() {
            // given
            Long memberId = 300L;
            Long adminId = 999L;
            ScreeningApproveRequest request = new ScreeningApproveRequest(memberId);

            Screening screening = mock(Screening.class);
            when(screeningCommandRepository.findByMemberId(memberId)).thenReturn(Optional.of(screening));

            // when
            screeningService.approve(request, adminId);

            // then
            verify(screeningCommandRepository).findByMemberId(memberId);
            verify(screening).approve(adminId);
        }

        @Test
        @DisplayName("존재하지 않는 MemberScreening이면 MemberScreeningNotFoundException을 던집니다.")
        void throwExceptionWhenScreeningDoesNotExist() {
            // given
            Long memberId = 400L;
            Long adminId = 999L;
            ScreeningApproveRequest request = new ScreeningApproveRequest(memberId);

            when(screeningCommandRepository.findByMemberId(memberId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> screeningService.approve(request, adminId))
                    .isInstanceOf(ScreeningNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("reject() 테스트")
    class Reject {

        @Test
        @DisplayName("존재하는 MemberScreening이면 reject(adminId, reasonType)을 호출합니다.")
        void rejectWhenScreeningExists() {
            // given
            Long memberId = 500L;
            Long adminId = 999L;
            String rejectionReason = "STOLEN_IMAGE";
            ScreeningRejectRequest request = new ScreeningRejectRequest(memberId, rejectionReason);

            Screening screening = mock(Screening.class);
            when(screeningCommandRepository.findByMemberId(memberId)).thenReturn(Optional.of(screening));

            // when
            screeningService.reject(request, adminId);

            // then
            verify(screeningCommandRepository).findByMemberId(memberId);
            verify(screening).reject(adminId, RejectionReasonType.STOLEN_IMAGE);
        }

        @Test
        @DisplayName("존재하지 않는 MemberScreening이면 MemberScreeningNotFoundException을 던집니다.")
        void throwExceptionWhenRejectingNonExistentScreening() {
            // given
            Long memberId = 600L;
            Long adminId = 999L;
            ScreeningRejectRequest request = new ScreeningRejectRequest(memberId, "STOLEN_IMAGE");

            when(screeningCommandRepository.findByMemberId(memberId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> screeningService.reject(request, adminId))
                    .isInstanceOf(ScreeningNotFoundException.class);
        }

        @Test
        @DisplayName("존재하지 않는 반려 사유인 경우 InvalidRejectionReasonException을 던집니다.")
        void throwExceptionWhenRejectionReasonNonExists() {
            // given
            Long memberId = 600L;
            Long adminId = 999L;
            ScreeningRejectRequest request = new ScreeningRejectRequest(memberId, "NON_EXISTING_REASON");

            Screening screening = mock(Screening.class);
            when(screeningCommandRepository.findByMemberId(memberId)).thenReturn(Optional.of(screening));

            // when & then
            assertThatThrownBy(() -> screeningService.reject(request, adminId))
                    .isInstanceOf(InvalidRejectionReasonException.class);
        }
    }
}

