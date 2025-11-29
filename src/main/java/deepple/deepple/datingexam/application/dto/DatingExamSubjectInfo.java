package deepple.deepple.datingexam.application.dto;

import com.querydsl.core.annotations.QueryProjection;
import deepple.deepple.datingexam.domain.SubjectType;
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
