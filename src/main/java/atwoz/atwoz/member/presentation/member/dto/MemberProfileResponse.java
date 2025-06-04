package atwoz.atwoz.member.presentation.member.dto;

import atwoz.atwoz.member.query.member.infra.view.InterviewResultView;
import atwoz.atwoz.member.query.member.infra.view.MatchInfo;

import java.util.List;

public record MemberProfileResponse(
    BasicInfo basicMemberInfo,
    MatchInfo matchInfo,
    List<InterviewResultView> interviews
) {
}
