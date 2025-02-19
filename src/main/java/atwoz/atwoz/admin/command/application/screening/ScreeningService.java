package atwoz.atwoz.admin.command.application.screening;

import atwoz.atwoz.admin.command.domain.screening.Screening;
import atwoz.atwoz.admin.command.domain.screening.ScreeningCommandRepository;
import atwoz.atwoz.admin.presentation.screening.ScreeningRejectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            log.warn("멤버(id: {})에 대해 중복된 Screening을 생성할 수 없습니다.", memberId);
            return;
        }
        screeningCommandRepository.save(Screening.from(memberId));
    }

    @Transactional
    public void approve(long screeningId, long adminId) {
        Screening screening = findById(screeningId);
        screening.approve(adminId);
    }

    @Transactional
    public void reject(long screeningId, long adminId, ScreeningRejectRequest request) {
        Screening screening = findById(screeningId);
        screening.reject(adminId, toRejectionReasonType(request.rejectionReason()));
    }

    private Screening findById(long id) {
        return screeningCommandRepository.findById(id)
                .orElseThrow(ScreeningNotFoundException::new);
    }
}
