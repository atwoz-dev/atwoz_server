package atwoz.atwoz.admin.command.application.screening;

import atwoz.atwoz.admin.command.domain.screening.Screening;
import atwoz.atwoz.admin.command.domain.screening.ScreeningCommandRepository;
import atwoz.atwoz.admin.command.domain.screening.ScreeningNotFoundException;
import atwoz.atwoz.admin.presentation.screening.dto.ScreeningApproveRequest;
import atwoz.atwoz.admin.presentation.screening.dto.ScreeningRejectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScreeningService {

    private final ScreeningCommandRepository screeningCommandRepository;

    @Transactional
    public void create(Long memberId) {
        if (screeningCommandRepository.existsByMemberId(memberId)) {
            log.warn("멤버(id: {})에 대해 중복된 MemberScreening을 생성할 수 없습니다.", memberId);
            return;
        }
        screeningCommandRepository.save(Screening.from(memberId));
    }

    @Transactional
    public void approve(ScreeningApproveRequest request, Long adminId) {
        Screening screening = findScreening(request.memberId());
        screening.approve(adminId);
    }

    @Transactional
    public void reject(ScreeningRejectRequest request, Long adminId) {
        Screening screening = findScreening(request.memberId());
        screening.reject(adminId, ScreeningMapper.toRejectionReasonType(request.rejectionReason()));
    }

    private Screening findScreening(Long memberId) {
        return screeningCommandRepository.findByMemberId(memberId)
                .orElseThrow(() -> new ScreeningNotFoundException(memberId));
    }
}
