package atwoz.atwoz.admin.command.application.warning;

import atwoz.atwoz.admin.command.domain.warning.Warning;
import atwoz.atwoz.admin.command.domain.warning.WarningCommandRepository;
import atwoz.atwoz.admin.presentation.warning.WarningCreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static atwoz.atwoz.admin.command.application.warning.WarningMapper.toWarningReasonType;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarningService {

    private final WarningCommandRepository warningCommandRepository;

    @Transactional
    public void issue(long adminId, WarningCreateRequest request) {
        final long memberId = request.memberId();

        var warning = Warning.issue(adminId, memberId, toWarningReasonType(request.reasonType()));
        warningCommandRepository.saveAndFlush(warning);
        log.info("멤버 id: {}에 대한 경고 발행(id: {})", memberId, warning.getId());

        long totalWarnings = warningCommandRepository.countByMemberId(memberId);
        warning.checkIfThresholdExceeded(totalWarnings);
    }
}
