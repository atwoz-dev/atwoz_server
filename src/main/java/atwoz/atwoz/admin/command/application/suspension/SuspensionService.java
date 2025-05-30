package atwoz.atwoz.admin.command.application.suspension;

import atwoz.atwoz.admin.command.domain.suspension.*;
import atwoz.atwoz.common.event.Events;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static atwoz.atwoz.admin.command.application.suspension.SuspensionMapper.toSuspensionStatus;

@Service
@RequiredArgsConstructor
public class SuspensionService {

    private final SuspensionCommandRepository suspensionCommandRepository;
    private final SuspensionPolicy suspensionPolicy;

    @Transactional
    public void updateStatusByAdmin(long adminId, SuspendRequest request) {
        var suspension = createOrUpdateSuspension(adminId, request.memberId(), toSuspensionStatus(request.status()));
        suspensionCommandRepository.save(suspension);
        Events.raise(MemberSuspendedEvent.of(request.memberId(), request.status()));
    }

    @Transactional
    public void evaluateAndSuspend(long adminId, long memberId, long currentWarningCount) {
        SuspensionStatus status = suspensionPolicy.evaluate(currentWarningCount);
        var suspension = createOrUpdateSuspension(adminId, memberId, status);
        suspensionCommandRepository.save(suspension);
        Events.raise(MemberSuspendedEvent.of(memberId, status.toString()));
    }

    private Suspension createOrUpdateSuspension(long adminId, long memberId, SuspensionStatus status) {
        return suspensionCommandRepository
            .findByMemberId(memberId)
            .map(existing -> {
                existing.updateStatus(adminId, status);
                return existing;
            })
            .orElseGet(() -> Suspension.of(adminId, memberId, status));
    }
}
