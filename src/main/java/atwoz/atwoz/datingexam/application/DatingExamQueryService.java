package atwoz.atwoz.datingexam.application;

import atwoz.atwoz.datingexam.application.dto.DatingExamInfoResponse;
import atwoz.atwoz.datingexam.application.provided.DatingExamFinder;
import atwoz.atwoz.datingexam.application.required.DatingExamQueryRepository;
import atwoz.atwoz.datingexam.domain.SubjectType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Transactional
@Validated
@RequiredArgsConstructor
public class DatingExamQueryService implements DatingExamFinder {
    private final DatingExamQueryRepository datingExamQueryRepository;

    @Override
    public DatingExamInfoResponse findRequiredExamInfo() {
        return datingExamQueryRepository.findDatingExamInfo(SubjectType.REQUIRED);
    }

    @Override
    public DatingExamInfoResponse findOptionalExamInfo() {
        return datingExamQueryRepository.findDatingExamInfo(SubjectType.OPTIONAL);
    }
}
