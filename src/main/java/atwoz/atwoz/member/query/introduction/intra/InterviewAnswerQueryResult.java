package atwoz.atwoz.member.query.introduction.intra;

import com.querydsl.core.annotations.QueryProjection;

public record InterviewAnswerQueryResult(
    long memberId,
    String content
) {
    @QueryProjection
    public InterviewAnswerQueryResult {
    }
}
