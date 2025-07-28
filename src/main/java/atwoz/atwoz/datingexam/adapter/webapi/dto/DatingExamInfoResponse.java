package atwoz.atwoz.datingexam.adapter.webapi.dto;

import com.querydsl.core.annotations.QueryProjection;

import java.util.List;

public record DatingExamInfoResponse(
    List<DatingExamSubjectInfo> subjects
) {
    @QueryProjection
    public DatingExamInfoResponse {
    }
}
