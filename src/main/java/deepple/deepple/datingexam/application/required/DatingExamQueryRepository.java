package deepple.deepple.datingexam.application.required;

import deepple.deepple.datingexam.application.dto.DatingExamInfoResponse;
import deepple.deepple.datingexam.domain.SubjectType;

public interface DatingExamQueryRepository {
    DatingExamInfoResponse findDatingExamInfo(SubjectType subjectType);
}
