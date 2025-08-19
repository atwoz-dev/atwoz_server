package atwoz.atwoz.datingexam.application;

import atwoz.atwoz.datingexam.application.provided.SoulmateFinder;
import atwoz.atwoz.datingexam.application.required.DatingExamSubmitRepository;
import atwoz.atwoz.datingexam.application.required.SoulmateQueryRepository;
import atwoz.atwoz.datingexam.domain.DatingExamSubmit;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Set;


@Service
@Transactional
@Validated
@RequiredArgsConstructor
public class SoulmateQueryService implements SoulmateFinder {
    private final DatingExamSubmitRepository datingExamSubmitRepository;
    private final SoulmateQueryRepository soulmateQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public Set<Long> findSoulmateIds(Long memberId) {
        DatingExamSubmit datingExamSubmit = getDatingExamSubmit(memberId);
        validateDatingExamSubmit(datingExamSubmit);
        Set<Long> soulmateIds = soulmateQueryRepository.findSoulmateIds(memberId,
            datingExamSubmit.getRequiredSubjectAnswers());
        return soulmateIds;
    }

    private DatingExamSubmit getDatingExamSubmit(Long memberId) {
        return datingExamSubmitRepository.findByMemberId(memberId)
            .orElseThrow(() -> new EntityNotFoundException("연애 모의고사 제출 기록이 없습니다. 회원 ID: " + memberId));
    }

    private void validateDatingExamSubmit(DatingExamSubmit datingExamSubmit) {
        if (!datingExamSubmit.isRequiredSubjectSubmitted()) {
            throw new IllegalStateException("필수 과목을 먼저 제출해야 합니다. datingExamSubmitId: " + datingExamSubmit.getId());
        }
    }
}
