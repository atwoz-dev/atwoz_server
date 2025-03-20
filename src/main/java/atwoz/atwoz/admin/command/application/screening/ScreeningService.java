package atwoz.atwoz.admin.command.application.screening;

import atwoz.atwoz.admin.command.domain.screening.Screening;
import atwoz.atwoz.admin.command.domain.screening.ScreeningCommandRepository;
import atwoz.atwoz.admin.presentation.screening.ScreeningApproveRequest;
import atwoz.atwoz.admin.presentation.screening.ScreeningRejectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static atwoz.atwoz.admin.command.application.screening.ScreeningMapper.toRejectionReasonType;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScreeningService {

    private final ScreeningCommandRepository screeningCommandRepository;

    @Transactional
    public void create(long memberId) {
        if (screeningCommandRepository.existsByMemberId(memberId)) {
            throw new DuplicateScreeningException(memberId);
        }
        screeningCommandRepository.save(Screening.from(memberId));
    }

    @Transactional
    public void approve(long screeningId, ScreeningApproveRequest request, long adminId) {
        Screening screening = getScreening(screeningId);

        if (screening.hasVersionConflict(request.version())) {
            throw new OptimisticLockingFailureException("심사를 승인할 수 없습니다.");
        }

        screening.approve(adminId);
    }

    @Transactional
    public void reject(long screeningId, long adminId, ScreeningRejectRequest request) {
        Screening screening = getScreening(screeningId);

        if (screening.hasVersionConflict(request.version())) {
            throw new OptimisticLockingFailureException("심사를 반려할 수 없습니다.");
        }

        screening.reject(adminId, toRejectionReasonType(request.rejectionReason()));
    }

    private Screening getScreening(long id) {
        return screeningCommandRepository.findById(id)
                .orElseThrow(ScreeningNotFoundException::new);
    }
}
