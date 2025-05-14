package atwoz.atwoz.admin.command.application.warning;

import atwoz.atwoz.admin.command.domain.warning.Warning;
import atwoz.atwoz.admin.command.domain.warning.WarningCommandRepository;
import atwoz.atwoz.admin.presentation.warning.WarningCreatedRequest;
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
    public Long issue(long adminId, WarningCreatedRequest request) {
        var warning = Warning.of(adminId, request.memberId(), toWarningReasonType(request.reasonType()));
        warningCommandRepository.save(warning);
        log.info("멤버 id: {}에 대한 경고 발행(id: {})", warning.getMemberId(), warning.getId());
        return warning.getId();
    }
}
