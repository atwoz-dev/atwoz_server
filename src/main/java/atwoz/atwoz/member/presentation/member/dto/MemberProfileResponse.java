package atwoz.atwoz.member.presentation.member.dto;

import atwoz.atwoz.member.query.member.view.BasicMemberInfo;
import atwoz.atwoz.member.query.member.view.InterviewResultView;
import atwoz.atwoz.member.query.member.view.MatchInfo;
import atwoz.atwoz.member.query.member.view.OtherMemberProfileView;

import java.util.List;

public record MemberProfileResponse(
        BasicMemberInfo basicMemberInfo,
        MatchInfo matchInfo,
        List<InterviewResultView> interviews
) {
    public MemberProfileResponse(OtherMemberProfileView otherMemberProfileView, List<InterviewResultView> interviews) {
        this(otherMemberProfileView.basicMemberInfo(), otherMemberProfileView.matchInfo().matchId() == null ? null : otherMemberProfileView.matchInfo(), interviews);
    }
}
