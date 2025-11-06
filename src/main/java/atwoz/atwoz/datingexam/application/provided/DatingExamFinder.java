package atwoz.atwoz.datingexam.application.provided;

import atwoz.atwoz.datingexam.application.dto.DatingExamInfoWithSubjectSubmissionResponse;

public interface DatingExamFinder {
    DatingExamInfoWithSubjectSubmissionResponse findRequiredExamInfo(Long memberId);

    DatingExamInfoWithSubjectSubmissionResponse findOptionalExamInfo(Long memberId);
}
