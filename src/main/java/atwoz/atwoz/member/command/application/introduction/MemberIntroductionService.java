package atwoz.atwoz.member.command.application.introduction;

import atwoz.atwoz.member.command.application.introduction.exception.IntroducedMemberNotActiveException;
import atwoz.atwoz.member.command.application.introduction.exception.IntroducedMemberNotFoundException;
import atwoz.atwoz.member.command.application.introduction.exception.MemberIntroductionAlreadyExistsException;
import atwoz.atwoz.member.command.domain.introduction.IntroductionType;
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
    public void createGradeIntroduction(long memberId, long introducedMemberId) {
        createIntroduction(memberId, introducedMemberId, IntroductionType.DIAMOND_GRADE);
    }

    @Transactional
    public void createHobbyIntroduction(long memberId, long introducedMemberId) {
        createIntroduction(memberId, introducedMemberId, IntroductionType.SAME_HOBBY);
    }

    @Transactional
    public void createReligionIntroduction(long memberId, long introducedMemberId) {
        createIntroduction(memberId, introducedMemberId, IntroductionType.SAME_RELIGION);
    }

    @Transactional
    public void createCityIntroduction(long memberId, long introducedMemberId) {
        createIntroduction(memberId, introducedMemberId, IntroductionType.SAME_CITY);
    }

    @Transactional
    public void createRecentIntroduction(long memberId, long introducedMemberId) {
        createIntroduction(memberId, introducedMemberId, IntroductionType.RECENTLY_JOINED);
    }

    @Transactional
    public void createSoulmateIntroduction(long memberId, long introducedMemberId) {
        createIntroduction(memberId, introducedMemberId, IntroductionType.SOULMATE);
    }

    private void createIntroduction(long memberId, long introducedMemberId, IntroductionType introductionType) {
        validateIntroduction(memberId, introducedMemberId);
        MemberIntroduction memberIntroduction = MemberIntroduction.of(memberId, introducedMemberId, introductionType);
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
