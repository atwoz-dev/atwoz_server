package atwoz.atwoz.datingexam.adapter.webapi.dto;

import com.querydsl.core.annotations.QueryProjection;

public record DatingExamAnswerInfo(
    long id,
    String content
) {
    @QueryProjection
    public DatingExamAnswerInfo {
    }
}
