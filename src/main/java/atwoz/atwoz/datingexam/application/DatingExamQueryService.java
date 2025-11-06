package atwoz.atwoz.datingexam.application;

import atwoz.atwoz.datingexam.application.dto.DatingExamInfoResponse;
import atwoz.atwoz.datingexam.application.dto.DatingExamInfoWithSubjectSubmissionResponse;
import atwoz.atwoz.datingexam.application.provided.DatingExamFinder;
import atwoz.atwoz.datingexam.application.required.DatingExamQueryRepository;
import atwoz.atwoz.datingexam.application.required.DatingExamSubmitRepository;
import atwoz.atwoz.datingexam.domain.DatingExamSubmit;
import atwoz.atwoz.datingexam.domain.SubjectType;
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
