package atwoz.atwoz.member.command.application.introduction;

import atwoz.atwoz.member.command.application.introduction.exception.MemberIntroductionAlreadyExistsException;
import atwoz.atwoz.member.command.domain.introduction.MemberIntroduction;
import atwoz.atwoz.member.command.domain.introduction.MemberIntroductionCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberIntroductionService {
    private final MemberIntroductionCommandRepository memberIntroductionCommandRepository;

    @Transactional
    public void create(long memberId, long introducedMemberId) {
        if (memberIntroductionCommandRepository.existsByMemberIdAndIntroducedMemberId(memberId, introducedMemberId)) {
            throw new MemberIntroductionAlreadyExistsException();
        }
        MemberIntroduction memberIntroduction = MemberIntroduction.of(memberId, introducedMemberId);
        memberIntroductionCommandRepository.save(memberIntroduction);
    }
}
