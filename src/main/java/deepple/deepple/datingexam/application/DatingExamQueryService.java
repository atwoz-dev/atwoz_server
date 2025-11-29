package deepple.deepple.datingexam.application;

import deepple.deepple.datingexam.application.dto.DatingExamInfoResponse;
import deepple.deepple.datingexam.application.dto.DatingExamInfoWithSubjectSubmissionResponse;
import deepple.deepple.datingexam.application.provided.DatingExamFinder;
import deepple.deepple.datingexam.application.required.DatingExamQueryRepository;
import deepple.deepple.datingexam.application.required.DatingExamSubmitRepository;
import deepple.deepple.datingexam.domain.DatingExamSubmit;
import deepple.deepple.datingexam.domain.SubjectType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Set;

@Service
@Transactional
@Validated
@RequiredArgsConstructor
public class DatingExamQueryService implements DatingExamFinder {
    private final DatingExamSubmitRepository datingExamSubmitRepository;
    private final DatingExamQueryRepository datingExamQueryRepository;

    @Override
    public DatingExamInfoWithSubjectSubmissionResponse findRequiredExamInfo(Long memberId) {
        DatingExamInfoResponse datingExamInfo = datingExamQueryRepository.findDatingExamInfo(SubjectType.REQUIRED);
        Set<DatingExamSubmit> submittedExams = datingExamSubmitRepository.findAllByMemberId(memberId);
        return new DatingExamInfoWithSubjectSubmissionResponse(datingExamInfo, submittedExams);
    }

    @Override
    public DatingExamInfoWithSubjectSubmissionResponse findOptionalExamInfo(Long memberId) {
        DatingExamInfoResponse datingExamInfo = datingExamQueryRepository.findDatingExamInfo(SubjectType.OPTIONAL);
        Set<DatingExamSubmit> submittedExams = datingExamSubmitRepository.findAllByMemberId(memberId);
        return new DatingExamInfoWithSubjectSubmissionResponse(datingExamInfo, submittedExams);
    }
}
