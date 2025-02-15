package atwoz.atwoz.admin.command.application.memberscreening;

import atwoz.atwoz.admin.command.domain.memberscreening.MemberScreening;
import atwoz.atwoz.admin.command.domain.memberscreening.MemberScreeningCommandRepository;
import atwoz.atwoz.admin.command.domain.memberscreening.MemberScreeningNotFoundException;
import atwoz.atwoz.admin.command.domain.memberscreening.RejectionReasonType;
import atwoz.atwoz.admin.presentation.memberscreening.dto.MemberScreeningApproveRequest;
import atwoz.atwoz.admin.presentation.memberscreening.dto.MemberScreeningRejectRequest;
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
class MemberScreeningServiceTest {

    @Mock
    private MemberScreeningCommandRepository memberScreeningCommandRepository;

    @InjectMocks
    private MemberScreeningService memberScreeningService;

    @Nested
    @DisplayName("create() 테스트")
    class Create {

        @Test
        @DisplayName("멤버 심사가 아직 존재하지 않는다면 MemberScreening을 생성하고 저장합니다.")
        void createNewMemberScreeningWhenNotExists() {
            // given
            Long memberId = 100L;
            when(memberScreeningCommandRepository.existsByMemberId(memberId)).thenReturn(false);

            // when
            memberScreeningService.create(memberId);

            // then
            verify(memberScreeningCommandRepository).existsByMemberId(memberId);
            verify(memberScreeningCommandRepository).save(any(MemberScreening.class));
        }

        @Test
        @DisplayName("이미 동일 멤버의 MemberScreening이 존재한다면 저장하지 않고 경고 로그를 남기고 종료합니다.")
        void skipCreationWhenAlreadyExists() {
            // given
            Long memberId = 200L;
            when(memberScreeningCommandRepository.existsByMemberId(memberId)).thenReturn(true);

            // when
            memberScreeningService.create(memberId);

            // then
            verify(memberScreeningCommandRepository).existsByMemberId(memberId);
            verify(memberScreeningCommandRepository, never()).save(any(MemberScreening.class));
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
            MemberScreeningApproveRequest request = new MemberScreeningApproveRequest(memberId);

            MemberScreening memberScreening = mock(MemberScreening.class);
            when(memberScreeningCommandRepository.findByMemberId(memberId)).thenReturn(Optional.of(memberScreening));

            // when
            memberScreeningService.approve(request, adminId);

            // then
            verify(memberScreeningCommandRepository).findByMemberId(memberId);
            verify(memberScreening).approve(adminId);
        }

        @Test
        @DisplayName("존재하지 않는 MemberScreening이면 MemberScreeningNotFoundException을 던집니다.")
        void throwExceptionWhenScreeningDoesNotExist() {
            // given
            Long memberId = 400L;
            Long adminId = 999L;
            MemberScreeningApproveRequest request = new MemberScreeningApproveRequest(memberId);

            when(memberScreeningCommandRepository.findByMemberId(memberId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberScreeningService.approve(request, adminId))
                    .isInstanceOf(MemberScreeningNotFoundException.class);
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
            MemberScreeningRejectRequest request = new MemberScreeningRejectRequest(memberId, rejectionReason);

            MemberScreening memberScreening = mock(MemberScreening.class);
            when(memberScreeningCommandRepository.findByMemberId(memberId)).thenReturn(Optional.of(memberScreening));

            // when
            memberScreeningService.reject(request, adminId);

            // then
            verify(memberScreeningCommandRepository).findByMemberId(memberId);
            verify(memberScreening).reject(adminId, RejectionReasonType.STOLEN_IMAGE);
        }

        @Test
        @DisplayName("존재하지 않는 MemberScreening이면 MemberScreeningNotFoundException을 던집니다.")
        void throwExceptionWhenRejectingNonExistentScreening() {
            // given
            Long memberId = 600L;
            Long adminId = 999L;
            MemberScreeningRejectRequest request = new MemberScreeningRejectRequest(memberId, "STOLEN_IMAGE");

            when(memberScreeningCommandRepository.findByMemberId(memberId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberScreeningService.reject(request, adminId))
                    .isInstanceOf(MemberScreeningNotFoundException.class);
        }

        @Test
        @DisplayName("존재하지 않는 반려 사유인 경우 InvalidRejectionReasonException을 던집니다.")
        void throwExceptionWhenRejectionReasonNonExists() {
            // given
            Long memberId = 600L;
            Long adminId = 999L;
            MemberScreeningRejectRequest request = new MemberScreeningRejectRequest(memberId, "NON_EXISTING_REASON");

            MemberScreening memberScreening = mock(MemberScreening.class);
            when(memberScreeningCommandRepository.findByMemberId(memberId)).thenReturn(Optional.of(memberScreening));

            // when & then
            assertThatThrownBy(() -> memberScreeningService.reject(request, adminId))
                    .isInstanceOf(InvalidRejectionReasonException.class);
        }
    }
}

