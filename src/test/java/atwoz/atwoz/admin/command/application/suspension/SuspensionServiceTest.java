package atwoz.atwoz.admin.command.application.suspension;

import atwoz.atwoz.admin.command.domain.suspension.Suspension;
import atwoz.atwoz.admin.command.domain.suspension.SuspensionCommandRepository;
import atwoz.atwoz.admin.command.domain.suspension.SuspensionPolicy;
import atwoz.atwoz.admin.command.domain.suspension.SuspensionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SuspensionServiceTest {

    @Mock
    private SuspensionCommandRepository suspensionCommandRepository;

    @Mock
    private SuspensionPolicy suspensionPolicy;

    @InjectMocks
    private SuspensionService suspensionService;

    @Test
    @DisplayName("updateStatusByAdmin(): 기존 Suspension 없으면 새로 생성된 상태를 저장한다")
    void updateStatusByAdminWhenNoExistingSuspension() {
        // given
        var adminId = 10L;
        var memberId = 20L;
        var request = new SuspendRequest(memberId, "PERMANENT");
        given(suspensionCommandRepository.findByMemberId(memberId))
            .willReturn(Optional.empty());

        // when
        suspensionService.updateStatusByAdmin(adminId, request);

        // then
        var captor = ArgumentCaptor.forClass(Suspension.class);
        verify(suspensionCommandRepository).save(captor.capture());
        var saved = captor.getValue();
        assertThat(saved.getAdminId()).isEqualTo(adminId);
        assertThat(saved.getMemberId()).isEqualTo(memberId);
        assertThat(saved.getStatus()).isEqualTo(SuspensionStatus.PERMANENT);
    }

    @Test
    @DisplayName("updateStatusByAdmin(): 기존 Suspension 있으면 해당 엔티티의 상태가 변경된다")
    void updateStatusByAdminWhenExistingSuspension() {
        // given
        var adminId = 11L;
        var memberId = 21L;
        var existing = Suspension.of(1L, memberId, SuspensionStatus.TEMPORARY);
        var request = new SuspendRequest(memberId, "PERMANENT");
        given(suspensionCommandRepository.findByMemberId(memberId))
            .willReturn(Optional.of(existing));

        // when
        suspensionService.updateStatusByAdmin(adminId, request);

        // then
        assertThat(existing.getAdminId()).isEqualTo(adminId);
        assertThat(existing.getStatus()).isEqualTo(SuspensionStatus.PERMANENT);
    }

    @Test
    @DisplayName("evaluateAndSuspend(): policy가 TEMPORARY 반환 시 새로 생성된 Temporary 상태를 저장한다")
    void evaluateAndSuspendWhenPolicyReturnsTemporary() {
        // given
        var adminId = 12L;
        var memberId = 22L;
        var warningCount = 2L;
        given(suspensionPolicy.evaluate(warningCount))
            .willReturn(SuspensionStatus.TEMPORARY);
        given(suspensionCommandRepository.findByMemberId(memberId))
            .willReturn(Optional.empty());

        // when
        suspensionService.evaluateAndSuspend(adminId, memberId, warningCount);

        // then
        var captor = ArgumentCaptor.forClass(Suspension.class);
        verify(suspensionCommandRepository).save(captor.capture());
        var saved = captor.getValue();
        assertThat(saved.getAdminId()).isEqualTo(adminId);
        assertThat(saved.getStatus()).isEqualTo(SuspensionStatus.TEMPORARY);
    }

    @Test
    @DisplayName("evaluateAndSuspend(): policy가 PERMANENT 반환 시 기존 엔티티가 Permanent 상태로 업데이트된다")
    void evaluateAndSuspendWhenPolicyReturnsPermanent() {
        // given
        var adminId = 13L;
        var memberId = 23L;
        var warningCount = 5L;
        var existing = Suspension.of(1L, memberId, SuspensionStatus.TEMPORARY);
        given(suspensionPolicy.evaluate(warningCount))
            .willReturn(SuspensionStatus.PERMANENT);
        given(suspensionCommandRepository.findByMemberId(memberId))
            .willReturn(Optional.of(existing));

        // when
        suspensionService.evaluateAndSuspend(adminId, memberId, warningCount);

        // then
        assertThat(existing.getAdminId()).isEqualTo(adminId);
        assertThat(existing.getStatus()).isEqualTo(SuspensionStatus.PERMANENT);
    }
}
