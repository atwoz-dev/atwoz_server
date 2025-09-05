package atwoz.atwoz.datingexam.application;

import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.datingexam.application.dto.AllRequiredSubjectSubmittedEvent;
import atwoz.atwoz.datingexam.application.dto.DatingExamInfoResponse;
import atwoz.atwoz.datingexam.application.provided.DatingExamSubmitter;
import atwoz.atwoz.datingexam.application.required.DatingExamQueryRepository;
import atwoz.atwoz.datingexam.application.required.DatingExamSubjectRepository;
import atwoz.atwoz.datingexam.application.required.DatingExamSubmitRepository;
import atwoz.atwoz.datingexam.domain.DatingExamAnswerEncoder;
import atwoz.atwoz.datingexam.domain.DatingExamSubject;
import atwoz.atwoz.datingexam.domain.DatingExamSubmit;
import atwoz.atwoz.datingexam.domain.SubjectType;
import atwoz.atwoz.datingexam.domain.dto.DatingExamSubmitRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Set;

@Service
@Transactional
@Validated
@RequiredArgsConstructor
public class DatingExamModifyService implements DatingExamSubmitter {
    private final DatingExamSubjectRepository datingExamSubjectRepository;
    private final DatingExamSubmitRepository datingExamSubmitRepository;
    private final DatingExamQueryRepository datingExamQueryRepository;
    private final DatingExamAnswerEncoder answerEncoder;

    @Override
    @Transactional
    public void submitSubject(DatingExamSubmitRequest submitRequest, long memberId) {
        DatingExamSubject subject = getDatingExamSubject(submitRequest.subjectId());
        validateSubmit(submitRequest, subject, memberId);
        DatingExamSubmit datingExamSubmit = DatingExamSubmit.from(submitRequest, answerEncoder, memberId);
        datingExamSubmitRepository.save(datingExamSubmit);
        checkAndPublishAllRequiredSubjectsSubmitted(subject, memberId);
    }

    private void validateSubmit(DatingExamSubmitRequest submitRequest, DatingExamSubject subject, Long memberId) {
        if (datingExamSubmitRepository.existsByMemberIdAndSubjectId(memberId, submitRequest.subjectId())) {
            throw new IllegalStateException("이미 제출한 과목입니다.");
        }
        DatingExamInfoResponse validDatingExamInfo = datingExamQueryRepository
            .findDatingExamInfo(subject.getType());
        DatingExamSubmitRequestValidator.validateSubmit(submitRequest, validDatingExamInfo);
    }

    private DatingExamSubject getDatingExamSubject(Long subjectId) {
        return datingExamSubjectRepository.findById(subjectId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 과목입니다. subjectId: " + subjectId));
    }

    private void checkAndPublishAllRequiredSubjectsSubmitted(DatingExamSubject subject, Long memberId) {
        if (subject.isRequired() == false) {
            return;
        }

        if (isAllRequiredSubjectsSubmitted(memberId)) {
            Events.raise(AllRequiredSubjectSubmittedEvent.of(memberId));
        }
    }

    private boolean isAllRequiredSubjectsSubmitted(Long memberId) {
        Set<DatingExamSubject> requiredSubjects = datingExamSubjectRepository.findAllByType(
            SubjectType.REQUIRED);
        Set<DatingExamSubmit> submits = datingExamSubmitRepository.findAllByMemberId(memberId);

        return requiredSubjects.stream()
            .allMatch(requiredSubject -> submits.stream()
                .anyMatch(submit -> submit.getSubjectId().equals(requiredSubject.getId())));
    }
}
