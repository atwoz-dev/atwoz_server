package atwoz.atwoz.member.query.introduction.application;

import java.util.List;

public record MemberIntroductionProfileView(
    long memberId,
    String profileImageUrl,
    List<String> hobbies,
    String mbti,
    String religion,
    String interviewAnswerContent,
    String likeLevel,
    boolean isIntroduced
) {
}
