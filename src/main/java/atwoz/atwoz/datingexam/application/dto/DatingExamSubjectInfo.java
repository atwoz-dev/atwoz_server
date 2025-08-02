package atwoz.atwoz.datingexam.application.dto;

import atwoz.atwoz.datingexam.domain.SubjectType;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record DatingExamSubjectInfo(
    long id,
    @Schema(implementation = SubjectType.class)
    String type,
    String name,
    List<DatingExamQuestionInfo> questions
) {
    @QueryProjection
    public DatingExamSubjectInfo {
    }
}
