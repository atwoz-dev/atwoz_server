package atwoz.atwoz.member.query.introduction.application;

import java.util.List;

public record MemberIntroductionProfileView(
    long memberId,
    String profileImageUrl,
    List<String> tags,
    String interviewAnswerContent,
    String likeLevel,
    boolean isIntroduced
) {
}
