package deepple.deepple.datingexam.application.provided;

import deepple.deepple.datingexam.application.dto.DatingExamInfoWithSubjectSubmissionResponse;

public interface DatingExamFinder {
    DatingExamInfoWithSubjectSubmissionResponse findRequiredExamInfo(Long memberId);

    DatingExamInfoWithSubjectSubmissionResponse findOptionalExamInfo(Long memberId);
}
