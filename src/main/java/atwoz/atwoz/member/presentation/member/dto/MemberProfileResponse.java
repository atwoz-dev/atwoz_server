package atwoz.atwoz.member.presentation.member.dto;

import atwoz.atwoz.member.query.member.view.InterviewResultView;
import atwoz.atwoz.member.query.member.view.MatchInfo;

import java.util.List;

public record MemberProfileResponse(
    MemberInfo memberInfo,
    MatchInfo matchInfo,
    List<InterviewResultView> interviews
) {
}
