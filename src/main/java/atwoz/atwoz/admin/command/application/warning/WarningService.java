package atwoz.atwoz.admin.command.application.warning;

import atwoz.atwoz.admin.command.domain.warning.Warning;
import atwoz.atwoz.admin.command.domain.warning.WarningCommandRepository;
import atwoz.atwoz.admin.presentation.warning.WarningCreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static atwoz.atwoz.admin.command.application.warning.WarningMapper.toWarningReasonTypes;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarningService {

    private final WarningCommandRepository warningCommandRepository;

    @Transactional
    public void issue(long adminId, WarningCreateRequest request) {
        long memberId = request.memberId();

        long currentWarningCount = warningCommandRepository.countByMemberId(memberId);
        if (request.isCritical()) {
            currentWarningCount += 1;
        }
        var warning = Warning.issue(
            adminId,
            memberId,
            currentWarningCount,
            toWarningReasonTypes(request.reasonTypes()),
            request.isCritical()
        );

        warningCommandRepository.save(warning);

        log.info("멤버(id: {})에 경고(id: {}) 발행", memberId, warning.getId());
    }
}
