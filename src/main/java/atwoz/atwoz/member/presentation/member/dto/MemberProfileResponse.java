package atwoz.atwoz.member.presentation.member.dto;

import atwoz.atwoz.member.query.member.view.InterviewResultView;
import atwoz.atwoz.member.query.member.view.MatchInfo;
import atwoz.atwoz.member.query.member.view.ProfileExchangeInfo;

import java.util.List;

public record MemberProfileResponse(
    MemberInfo memberInfo,
    MatchInfo matchInfo,
    ProfileExchangeInfo profileExchangeInfo,
    List<InterviewResultView> interviews
) {
}
