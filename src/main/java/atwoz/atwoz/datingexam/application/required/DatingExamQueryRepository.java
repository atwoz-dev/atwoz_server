package atwoz.atwoz.datingexam.application.required;

import atwoz.atwoz.datingexam.adapter.webapi.dto.DatingExamInfoResponse;
import atwoz.atwoz.datingexam.domain.SubjectType;

public interface DatingExamQueryRepository {
    DatingExamInfoResponse findDatingExamInfo(SubjectType subjectType);
}
