package atwoz.atwoz.admin.command.application.screening;

import atwoz.atwoz.admin.command.domain.screening.RejectionReasonType;
import atwoz.atwoz.admin.command.domain.screening.Screening;
import atwoz.atwoz.admin.command.domain.screening.ScreeningCommandRepository;
import atwoz.atwoz.admin.presentation.screening.ScreeningRejectRequest;
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
        @DisplayName("멤버 심사가 아직 존재하지 않는다면 Screening을 생성하고 저장합니다.")
        void createNewMemberScreeningWhenNotExists() {
            // given
            long memberId = 100L;
            when(screeningCommandRepository.existsByMemberId(memberId)).thenReturn(false);

            // when
            screeningService.create(memberId);

            // then
            verify(screeningCommandRepository).existsByMemberId(memberId);
            verify(screeningCommandRepository).save(any(Screening.class));
        }

        @Test
        @DisplayName("이미 동일 멤버의 Screening이 존재한다면 저장하지 않고 경고 로그를 남기고 종료합니다.")
        void skipCreationWhenAlreadyExists() {
            // given
            long memberId = 100L;
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
        @DisplayName("존재하는 Screening이면 approve(adminId)를 호출합니다.")
        void approveWhenScreeningExists() {
            // given
            long screeningId = 1L;
            long adminId = 999L;

            Screening screening = mock(Screening.class);
            when(screeningCommandRepository.findById(screeningId)).thenReturn(Optional.of(screening));

            // when
            screeningService.approve(screeningId, adminId);

            // then
            verify(screening).approve(adminId);
        }

        @Test
        @DisplayName("존재하지 않는 Screening이면 ScreeningNotFoundException을 던집니다.")
        void throwExceptionWhenScreeningDoesNotExist() {
            // given
            long screeningId = 1L;
            when(screeningCommandRepository.findById(screeningId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> screeningService.approve(screeningId, 999L))
                    .isInstanceOf(ScreeningNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("reject() 테스트")
    class Reject {

        @Test
        @DisplayName("존재하는 Screening이면 reject(adminId, rejectionReason)을 호출합니다.")
        void rejectWhenScreeningExists() {
            // given
            long screeningId = 1L;
            long adminId = 999L;

            Screening screening = mock(Screening.class);
            when(screeningCommandRepository.findById(screeningId)).thenReturn(Optional.of(screening));

            String rejectionReason = "STOLEN_IMAGE";
            ScreeningRejectRequest request = new ScreeningRejectRequest(rejectionReason);

            // when
            screeningService.reject(screeningId, adminId, request);

            // then
            verify(screening).reject(adminId, RejectionReasonType.STOLEN_IMAGE);
        }

        @Test
        @DisplayName("존재하지 않는 Screening이면 ScreeningNotFoundException을 던집니다.")
        void throwExceptionWhenRejectingNonExistentScreening() {
            // given
            long screeningId = 1L;
            when(screeningCommandRepository.findById(screeningId)).thenReturn(Optional.empty());

            ScreeningRejectRequest request = new ScreeningRejectRequest("STOLEN_IMAGE");

            // when & then
            assertThatThrownBy(() -> screeningService.reject(screeningId, 999L, request))
                    .isInstanceOf(ScreeningNotFoundException.class);
        }

        @Test
        @DisplayName("존재하지 않는 반려 사유인 경우 InvalidRejectionReasonException을 던집니다.")
        void throwExceptionWhenRejectionReasonNonExists() {
            // given
            long screeningId = 1L;
            Screening screening = mock(Screening.class);
            when(screeningCommandRepository.findById(screeningId)).thenReturn(Optional.of(screening));

            ScreeningRejectRequest request = new ScreeningRejectRequest("NON_EXISTING_REASON");

            // when & then
            assertThatThrownBy(() -> screeningService.reject(screeningId, 999L, request))
                    .isInstanceOf(InvalidRejectionReasonException.class);
        }
    }
}

