package atwoz.atwoz.datingexam.command.domain;

import atwoz.atwoz.common.entity.BaseEntity;
import atwoz.atwoz.datingexam.command.domain.exception.InvalidDatingExamSubmitAnswersException;
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

    private Long preferredSubjectId;

    private String requiredSubjectAnswers;

    private String preferredSubjectAnswers;

    private DatingExamSubmit(@NonNull Long memberId) {
        this.memberId = memberId;
    }

    public static DatingExamSubmit from(@NonNull Long memberId) {
        return new DatingExamSubmit(memberId);
    }

    public void submitRequiredSubjectAnswers(String requiredSubjectAnswers) {
        setRequiredSubjectAnswers(requiredSubjectAnswers);
    }

    public void submitPreferredSubjectAnswers(Long preferredSubjectId, String preferredSubjectAnswers) {
        setPreferredSubjectId(preferredSubjectId);
        setPreferredSubjectAnswers(preferredSubjectAnswers);
    }

    private void setRequiredSubjectAnswers(final @NonNull String requiredSubjectAnswers) {
        if (requiredSubjectAnswers.isBlank()) {
            throw new InvalidDatingExamSubmitAnswersException("Answers for required subjects cannot be null or blank");
        }
        this.requiredSubjectAnswers = requiredSubjectAnswers;
    }

    private void setPreferredSubjectAnswers(final @NonNull String preferredSubjectAnswers) {
        if (preferredSubjectAnswers.isBlank()) {
            throw new InvalidDatingExamSubmitAnswersException("Answers for preferred subjects cannot be null or blank");
        }
        this.preferredSubjectAnswers = preferredSubjectAnswers;
    }

    private void setPreferredSubjectId(final @NonNull Long preferredSubjectId) {
        this.preferredSubjectId = preferredSubjectId;
    }
}
