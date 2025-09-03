package atwoz.atwoz.member.command.application.introduction;

import atwoz.atwoz.datingexam.application.required.SoulmateQueryRepository;
import atwoz.atwoz.member.command.application.introduction.exception.IntroducedMemberNotActiveException;
import atwoz.atwoz.member.command.application.introduction.exception.IntroducedMemberNotFoundException;
import atwoz.atwoz.member.command.application.introduction.exception.MemberIntroductionAlreadyExistsException;
import atwoz.atwoz.member.command.domain.introduction.IntroductionType;
import atwoz.atwoz.member.command.domain.introduction.MemberIntroduction;
import atwoz.atwoz.member.command.domain.introduction.MemberIntroductionCommandRepository;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class MemberIntroductionService {
    private final MemberCommandRepository memberCommandRepository;
    private final MemberIntroductionCommandRepository memberIntroductionCommandRepository;
    private final SoulmateQueryRepository soulmateQueryRepository;

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
        validateSoulmateIntroduction(memberId, introducedMemberId);
        createIntroduction(memberId, introducedMemberId, IntroductionType.SOULMATE);
    }

    @Transactional
    public void createSameAnswerIntroduction(long memberId, long introducedMemberId) {
        validateSameAnswerIntroduction(memberId, introducedMemberId);
        createIntroduction(memberId, introducedMemberId, IntroductionType.SAME_ANSWER);
    }

    private void validateSoulmateIntroduction(long memberId, long introducedMemberId) {
        Member member = getMember(memberId);
        if (member.hasSubmittedDatingExam() == false) {
            throw new IllegalStateException("연애 모의고사를 제출하지 않은 회원은 소울메이트 소개를 받을 수 없습니다. memberId: " + memberId);
        }
        Set<Long> soulmateIds = soulmateQueryRepository.findSameAnswerMemberIds(memberId);
        if (!soulmateIds.contains(introducedMemberId)) {
            throw new IllegalArgumentException("소울메이트가 아닙니다. introducedMemberId: " + introducedMemberId);
        }
    }

    private void validateSameAnswerIntroduction(long memberId, long introducedMemberId) {
        Set<Long> soulmateIds = soulmateQueryRepository.findSameAnswerMemberIds(memberId);
        if (!soulmateIds.contains(introducedMemberId)) {
            throw new IllegalArgumentException("같은 답안을 제출한 멤버가 아닙니다. introducedMemberId: " + introducedMemberId);
        }
    }

    private Member getMember(long memberId) {
        return memberCommandRepository.findById(memberId)
            .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다. memberId: " + memberId));
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
