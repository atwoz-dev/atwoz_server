package deepple.deepple.admin.command.application.suspension;

import deepple.deepple.admin.command.domain.suspension.*;
import deepple.deepple.admin.presentation.suspension.SuspendRequest;
import deepple.deepple.common.event.Events;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

import static deepple.deepple.admin.command.application.suspension.SuspensionMapper.toSuspensionStatus;

@Service
@RequiredArgsConstructor
public class SuspensionService {

    private final SuspensionCommandRepository suspensionCommandRepository;
    private final SuspensionPolicy suspensionPolicy;
    private final TaskScheduler taskScheduler;

    @Transactional
    public void suspendByAdmin(long adminId, long memberId, SuspendRequest request) {
        SuspensionStatus requestedStatus = toSuspensionStatus(request.status());
        createOrUpdateSuspension(adminId, memberId, requestedStatus);
    }

    @Transactional
    public void suspendByWarningCount(long adminId, long memberId, long currentWarningCount) {
        SuspensionStatus evaluatedStatus = suspensionPolicy.evaluate(currentWarningCount);
        createOrUpdateSuspension(adminId, memberId, evaluatedStatus);
    }

    @Transactional
    public void delete(long memberId) {
        suspensionCommandRepository.deleteByMemberId(memberId);
        Events.raise(MemberUnsuspendedEvent.of(memberId));
    }

    private void createOrUpdateSuspension(long adminId, long memberId, SuspensionStatus status) {
        Optional<Suspension> existingSuspension = suspensionCommandRepository.findByMemberId(memberId);

        if (status == SuspensionStatus.TEMPORARY) {
            if (existingSuspension.isPresent()) {
                return;
            }
            Suspension newSuspension = Suspension.createTemporary(adminId, memberId);
            suspensionCommandRepository.save(newSuspension);

            registerTemporaryScheduler(newSuspension.getMemberId(), newSuspension.getExpireAt());
        }

        if (status == SuspensionStatus.PERMANENT) {
            existingSuspension.ifPresentOrElse(
                suspension -> suspension.changeToPermanent(adminId),
                () -> {
                    Suspension newSuspension = Suspension.createPermanent(adminId, memberId);
                    suspensionCommandRepository.save(newSuspension);
                }
            );
        }
    }

    private void registerTemporaryScheduler(long memberId, Instant expireAt) {
        if (expireAt == null || expireAt.isBefore(Instant.now())) {
            return;
        }

        taskScheduler.schedule(() -> {
            Optional<Suspension> suspension = suspensionCommandRepository.findByMemberId(memberId);
            if (suspension.isEmpty() || suspension.get().getStatus() == SuspensionStatus.PERMANENT) {
                return;
            }

            suspensionCommandRepository.deleteByMemberId(memberId);
            Events.raise(MemberUnsuspendedEvent.of(memberId));
        }, expireAt);
    }
}
