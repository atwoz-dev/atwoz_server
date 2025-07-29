package atwoz.atwoz.datingexam.application.dto;

import com.querydsl.core.annotations.QueryProjection;

public record DatingExamAnswerInfo(
    long id,
    String content
) {
    @QueryProjection
    public DatingExamAnswerInfo {
    }
}
