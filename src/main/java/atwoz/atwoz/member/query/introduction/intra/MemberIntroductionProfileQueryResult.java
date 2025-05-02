package atwoz.atwoz.member.query.introduction.intra;

import com.querydsl.core.annotations.QueryProjection;

import java.util.Set;

public record MemberIntroductionProfileQueryResult(
    long memberId,
    String profileImageUrl,
    Set<String> hobbies,
    String religion,
    String mbti,
    String likeLevel,
    boolean isIntroduced
) {
    @QueryProjection
    public MemberIntroductionProfileQueryResult {
    }
}
