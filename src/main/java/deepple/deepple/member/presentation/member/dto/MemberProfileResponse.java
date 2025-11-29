package deepple.deepple.member.presentation.member.dto;

import deepple.deepple.member.query.member.view.InterviewResultView;
import deepple.deepple.member.query.member.view.IntroductionInfo;
import deepple.deepple.member.query.member.view.MatchInfo;
import deepple.deepple.member.query.member.view.ProfileExchangeInfo;

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
