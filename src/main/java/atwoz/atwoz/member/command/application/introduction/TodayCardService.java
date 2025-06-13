package atwoz.atwoz.member.command.application.introduction;

import atwoz.atwoz.member.command.application.introduction.exception.IntroducedMemberNotActiveException;
import atwoz.atwoz.member.command.application.introduction.exception.IntroducedMemberNotFoundException;
import atwoz.atwoz.member.command.domain.introduction.IntroductionType;
import atwoz.atwoz.member.command.domain.introduction.MemberIntroduction;
import atwoz.atwoz.member.command.domain.introduction.MemberIntroductionCommandRepository;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class TodayCardService {
    private final MemberCommandRepository memberCommandRepository;
    private final MemberIntroductionCommandRepository memberIntroductionCommandRepository;

    @Transactional
    public void createTodayCardIntroductions(long memberId, Set<Long> todayCardMemberIds) {
        todayCardMemberIds.forEach(
            introducedMemberId -> createIntroduction(memberId, introducedMemberId, IntroductionType.TODAY_CARD));
    }

    private void createIntroduction(long memberId, long introducedMemberId, IntroductionType introductionType) {
        if (hasIntroduction(memberId, introducedMemberId)) {
            return;
        }
        validateIntroduction(introducedMemberId);
        MemberIntroduction memberIntroduction = MemberIntroduction.of(memberId, introducedMemberId, introductionType);
        memberIntroductionCommandRepository.save(memberIntroduction);
    }

    private boolean hasIntroduction(long memberId, long introducedMemberId) {
        return memberIntroductionCommandRepository.existsByMemberIdAndIntroducedMemberId(memberId, introducedMemberId);
    }

    private void validateIntroduction(long introducedMemberId) {
        Member introductionMember = memberCommandRepository.findById(introducedMemberId)
            .orElseThrow(IntroducedMemberNotFoundException::new);
        if (!introductionMember.isActive()) {
            throw new IntroducedMemberNotActiveException();
        }
    }
}
