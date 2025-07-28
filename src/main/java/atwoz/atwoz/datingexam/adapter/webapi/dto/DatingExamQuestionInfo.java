package atwoz.atwoz.datingexam.adapter.webapi.dto;

import com.querydsl.core.annotations.QueryProjection;

import java.util.List;

public record DatingExamQuestionInfo(
    long id,
    String content,
    List<DatingExamAnswerInfo> answers
) {
    @QueryProjection
    public DatingExamQuestionInfo {
    }
}
