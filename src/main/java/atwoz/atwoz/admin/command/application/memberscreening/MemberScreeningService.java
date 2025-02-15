package atwoz.atwoz.admin.command.application.memberscreening;

import atwoz.atwoz.admin.command.domain.memberscreening.MemberScreening;
import atwoz.atwoz.admin.command.domain.memberscreening.MemberScreeningCommandRepository;
import atwoz.atwoz.admin.command.domain.memberscreening.MemberScreeningNotFoundException;
import atwoz.atwoz.admin.presentation.memberscreening.dto.MemberScreeningApproveRequest;
import atwoz.atwoz.admin.presentation.memberscreening.dto.MemberScreeningRejectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberScreeningService {

    private final MemberScreeningCommandRepository memberScreeningCommandRepository;

    @Transactional
    public void create(Long memberId) {
        if (memberScreeningCommandRepository.existsByMemberId(memberId)) {
            log.warn("멤버(id: {})에 대해 중복된 MemberScreening을 생성할 수 없습니다.", memberId);
            return;
        }
        memberScreeningCommandRepository.save(MemberScreening.from(memberId));
    }

    @Transactional
    public void approve(MemberScreeningApproveRequest request, Long adminId) {
        MemberScreening memberScreening = findMemberScreening(request.memberId());
        memberScreening.approve(adminId);
    }

    @Transactional
    public void reject(MemberScreeningRejectRequest request, Long adminId) {
        MemberScreening memberScreening = findMemberScreening(request.memberId());
        memberScreening.reject(adminId, MemberScreeningMapper.toRejectionReasonType(request.rejectionReason()));
    }

    private MemberScreening findMemberScreening(Long memberId) {
        return memberScreeningCommandRepository.findByMemberId(memberId)
                .orElseThrow(() -> new MemberScreeningNotFoundException(memberId));
    }
}
