package atwoz.atwoz.member.query.introduction.intra;

import com.querydsl.core.annotations.QueryProjection;

import java.util.List;

public record MemberIntroductionProfileQueryResult(
        long memberId,
        String profileImageUrl,
        List<String> hobbies,
        String religion,
        String mbti,
        String likeLevel,
        boolean isIntroduced
) {
    @QueryProjection
    public MemberIntroductionProfileQueryResult {
    }
}
