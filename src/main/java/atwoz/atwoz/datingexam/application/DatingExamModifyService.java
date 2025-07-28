package atwoz.atwoz.datingexam.application;

import atwoz.atwoz.datingexam.adapter.webapi.dto.DatingExamInfoResponse;
import atwoz.atwoz.datingexam.application.exception.InvalidDatingExamSubmitRequestException;
import atwoz.atwoz.datingexam.application.provided.DatingExamSubmitter;
import atwoz.atwoz.datingexam.application.required.DatingExamQueryRepository;
import atwoz.atwoz.datingexam.application.required.DatingExamSubmitRepository;
import atwoz.atwoz.datingexam.domain.DatingExamAnswerEncoder;
import atwoz.atwoz.datingexam.domain.DatingExamSubmit;
import atwoz.atwoz.datingexam.domain.SubjectType;
import atwoz.atwoz.datingexam.domain.dto.DatingExamSubmitRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Transactional
@Validated
@RequiredArgsConstructor
public class DatingExamModifyService implements DatingExamSubmitter {
    private final DatingExamSubmitRepository datingExamSubmitRepository;
    private final DatingExamQueryRepository datingExamQueryRepository;
    private final DatingExamAnswerEncoder answerEncoder;

    @Override
    @Transactional
    public void submitRequiredSubject(DatingExamSubmitRequest submitRequest, long memberId) {
        if (datingExamSubmitRepository.existsByMemberId(memberId)) {
            throw new InvalidDatingExamSubmitRequestException("이미 필수 과목을 제출한 회원입니다.");
        }
        DatingExamInfoResponse validDatingExamInfo = datingExamQueryRepository.findDatingExamInfo(SubjectType.REQUIRED);
        DatingExamSubmitRequestValidator.validateSubmit(submitRequest, validDatingExamInfo, SubjectType.REQUIRED);
        DatingExamSubmit datingExamSubmit = DatingExamSubmit.from(memberId);
        datingExamSubmit.submitRequiredSubjectAnswers(submitRequest, answerEncoder);
        datingExamSubmitRepository.save(datingExamSubmit);
    }

    @Override
    @Transactional
    public void submitOptionalSubject(DatingExamSubmitRequest submitRequest, long memberId) {
        DatingExamSubmit datingExamSubmit = datingExamSubmitRepository.findByMemberId(memberId)
            .orElseThrow(() -> new InvalidDatingExamSubmitRequestException("필수 과목을 먼저 제출해야 합니다."));
        DatingExamInfoResponse validDatingExamInfo = datingExamQueryRepository.findDatingExamInfo(SubjectType.OPTIONAL);
        DatingExamSubmitRequestValidator.validateSubmit(submitRequest, validDatingExamInfo, SubjectType.OPTIONAL);
        datingExamSubmit.submitPreferredSubjectAnswers(submitRequest, answerEncoder);
        datingExamSubmitRepository.save(datingExamSubmit);
    }
}
