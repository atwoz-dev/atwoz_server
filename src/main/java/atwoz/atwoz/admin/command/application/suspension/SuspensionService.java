package atwoz.atwoz.admin.command.application.suspension;

import atwoz.atwoz.admin.command.domain.suspension.Suspension;
import atwoz.atwoz.admin.command.domain.suspension.SuspensionCommandRepository;
import atwoz.atwoz.admin.command.domain.suspension.SuspensionPolicy;
import atwoz.atwoz.admin.command.domain.suspension.SuspensionStatus;
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
    }

    @Transactional
    public void evaluateAndSuspend(long adminId, long memberId, long currentWarningCount) {
        SuspensionStatus status = suspensionPolicy.evaluate(currentWarningCount);
        var suspension = createOrUpdateSuspension(adminId, memberId, status);
        suspensionCommandRepository.save(suspension);
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
