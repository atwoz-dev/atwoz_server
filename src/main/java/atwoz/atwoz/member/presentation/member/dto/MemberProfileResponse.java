package atwoz.atwoz.member.presentation.member.dto;

import atwoz.atwoz.member.query.member.view.InterviewResultView;
import atwoz.atwoz.member.query.member.view.IntroductionInfo;
import atwoz.atwoz.member.query.member.view.MatchInfo;
import atwoz.atwoz.member.query.member.view.ProfileExchangeInfo;

import java.util.List;

public record MemberProfileResponse(
    MemberInfo memberInfo,
    MatchInfo matchInfo,
    ContactInfo contactInfo,
    ProfileExchangeInfo profileExchangeInfo,
    IntroductionInfo introductionInfo,
    List<InterviewResultView> interviews
) {
}
