package atwoz.atwoz.member.query.member.application;

import atwoz.atwoz.community.command.domain.profileexchange.ProfileExchangeStatus;
import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.presentation.member.MemberMapper;
import atwoz.atwoz.member.presentation.member.dto.MemberProfileResponse;
import atwoz.atwoz.member.query.member.application.exception.ProfileAccessDeniedException;
import atwoz.atwoz.member.query.member.infra.MemberQueryRepository;
import atwoz.atwoz.member.query.member.infra.view.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberQueryService {
    private final MemberQueryRepository memberQueryRepository;


    public MemberInfoView getInfoCache(Long memberId) {
        return memberQueryRepository.findInfoByMemberId(memberId).orElseThrow(MemberNotFoundException::new);
    }

    public MemberProfileView getProfile(Long memberId) {
        return memberQueryRepository.findProfileByMemberId(memberId).orElseThrow(MemberNotFoundException::new);
    }

    public MemberContactView getContact(Long memberId) {
        return memberQueryRepository.findContactsByMemberId(memberId).orElseThrow(MemberNotFoundException::new);
    }

    public HeartBalanceView getHeartBalance(Long memberId) {
        return memberQueryRepository.findHeartBalanceByMemberId(memberId).orElseThrow(MemberNotFoundException::new);
    }

    public MemberProfileResponse getMemberProfile(Long memberId, Long otherMemberId) {
        ProfileAccessView profileAccessView = memberQueryRepository.findProfileAccessViewByMemberId(memberId,
            otherMemberId).orElseThrow(MemberNotFoundException::new);
        validateProfileAccessView(profileAccessView, memberId);

        OtherMemberProfileView profileView = memberQueryRepository.findOtherProfileByMemberId(memberId, otherMemberId)
            .orElseThrow(MemberNotFoundException::new);

        List<InterviewResultView> interviewResultViews = memberQueryRepository.findInterviewsByMemberId(memberId);

        return new MemberProfileResponse(MemberMapper.toBasicInfo(profileView.basicMemberInfo()),
            profileView.matchInfo(), interviewResultViews);
    }

    private void validateProfileAccessView(ProfileAccessView profileAccessView, Long memberId) {
        if (profileAccessView.isIntroduced() == Boolean.TRUE) { // 소개를 받은 경우.
            return;
        }
        if (profileAccessView.likeReceived() == Boolean.TRUE) { // 좋아요를 받은 경우.
            return;
        }
        if (ProfileExchangeStatus.APPROVE.name().equals(profileAccessView.profileExchangeStaus())) { // 프로필 교환이 완료된 경우.
            return;
        }
        if (profileAccessView.responderId() == memberId) { // 프로필 교환 요청을 받은 경우.
            return;
        }
        throw new ProfileAccessDeniedException();
    }
}
