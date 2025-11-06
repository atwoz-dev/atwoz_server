package atwoz.atwoz.datingexam.application.dto;

import atwoz.atwoz.datingexam.domain.DatingExamSubmit;
import atwoz.atwoz.datingexam.domain.SubjectType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Set;

public record DatingExamSubjectInfoWithSubjectSubmission(
    long id,
    @Schema(implementation = SubjectType.class)
    String type,
    String name,
    boolean isSubmitted,
    List<DatingExamQuestionInfo> questions
) {
    public DatingExamSubjectInfoWithSubjectSubmission(
        DatingExamSubjectInfo subjectInfo,
        Set<DatingExamSubmit> submits
    ) {
        this(
            subjectInfo.id(),
            subjectInfo.type(),
            subjectInfo.name(),
            submits.stream().anyMatch(submit -> submit.getSubjectId() == subjectInfo.id()),
            subjectInfo.questions()
        );
    }
}
