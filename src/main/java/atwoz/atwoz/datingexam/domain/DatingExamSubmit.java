package atwoz.atwoz.datingexam.domain;

import atwoz.atwoz.common.entity.BaseEntity;
import atwoz.atwoz.datingexam.domain.dto.DatingExamSubmitRequest;
import atwoz.atwoz.datingexam.domain.exception.InvalidDatingExamSubmitAnswersException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
public class DatingExamSubmit extends BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private Long memberId;

    private String requiredSubjectAnswers;

    private String preferredSubjectAnswers;

    private DatingExamSubmit(@NonNull Long memberId) {
        this.memberId = memberId;
    }

    public static DatingExamSubmit from(@NonNull Long memberId) {
        return new DatingExamSubmit(memberId);
    }

    public void submitRequiredSubjectAnswers(DatingExamSubmitRequest request, DatingExamAnswerEncoder answerEncoder) {
        String encodedAnswers = answerEncoder.encode(request);
        setRequiredSubjectAnswers(encodedAnswers);
    }

    public void submitPreferredSubjectAnswers(DatingExamSubmitRequest request, DatingExamAnswerEncoder answerEncoder) {
        String encodedAnswers = answerEncoder.encode(request);
        setPreferredSubjectAnswers(encodedAnswers);
    }

    private void setRequiredSubjectAnswers(final @NonNull String requiredSubjectAnswers) {
        if (this.requiredSubjectAnswers != null) {
            throw new InvalidDatingExamSubmitAnswersException("필수 과목 답변이 이미 제출되었습니다");
        }
        if (requiredSubjectAnswers.isBlank()) {
            throw new InvalidDatingExamSubmitAnswersException("필수 과목 답변은 null 또는 빈 문자열일 수 없습니다");
        }
        this.requiredSubjectAnswers = requiredSubjectAnswers;
    }

    private void setPreferredSubjectAnswers(final @NonNull String preferredSubjectAnswers) {
        if (this.preferredSubjectAnswers != null) {
            throw new InvalidDatingExamSubmitAnswersException("선호 과목 답변이 이미 제출되었습니다");
        }
        if (preferredSubjectAnswers.isBlank()) {
            throw new InvalidDatingExamSubmitAnswersException("선호 과목 답변은 null 또는 빈 문자열일 수 없습니다");
        }
        this.preferredSubjectAnswers = preferredSubjectAnswers;
    }
}
