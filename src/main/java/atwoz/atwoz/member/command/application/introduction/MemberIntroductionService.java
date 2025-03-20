package atwoz.atwoz.member.command.application.introduction;

import atwoz.atwoz.member.command.application.introduction.exception.IntroducedMemberNotActiveException;
import atwoz.atwoz.member.command.application.introduction.exception.IntroducedMemberNotFoundException;
import atwoz.atwoz.member.command.application.introduction.exception.MemberIntroductionAlreadyExistsException;
import atwoz.atwoz.member.command.domain.introduction.MemberIntroduction;
import atwoz.atwoz.member.command.domain.introduction.MemberIntroductionCommandRepository;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberIntroductionService {
    private final MemberCommandRepository memberCommandRepository;
    private final MemberIntroductionCommandRepository memberIntroductionCommandRepository;

    @Transactional
    public void create(long memberId, long introducedMemberId) {
        validateIntroduction(memberId, introducedMemberId);
        MemberIntroduction memberIntroduction = MemberIntroduction.of(memberId, introducedMemberId);
        memberIntroductionCommandRepository.save(memberIntroduction);
    }

    private void validateIntroduction(long memberId, long introducedMemberId) {
        Member introductionMember = memberCommandRepository.findById(introducedMemberId)
                .orElseThrow(IntroducedMemberNotFoundException::new);
        if (!introductionMember.isActive()) {
            throw new IntroducedMemberNotActiveException();
        }
        if (memberIntroductionCommandRepository.existsByMemberIdAndIntroducedMemberId(memberId, introducedMemberId)) {
            throw new MemberIntroductionAlreadyExistsException();
        }
    }
}
