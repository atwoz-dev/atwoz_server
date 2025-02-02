package atwoz.atwoz.admin.command.application.memberscreening;

import atwoz.atwoz.admin.command.domain.memberscreening.MemberScreening;
import atwoz.atwoz.admin.command.domain.memberscreening.MemberScreeningCommandRepository;
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
            log.warn("이미 존재하는 MemberScreening 입니다. memberId={} ", memberId);
            return;
        }
        memberScreeningCommandRepository.save(MemberScreening.from(memberId));
    }
}
