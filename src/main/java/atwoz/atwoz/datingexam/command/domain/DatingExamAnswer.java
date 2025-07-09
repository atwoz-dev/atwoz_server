package atwoz.atwoz.datingexam.command.domain;

import atwoz.atwoz.common.entity.BaseEntity;
import atwoz.atwoz.datingexam.command.domain.exception.InvalidDatingExamAnswerContentException;
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
public class DatingExamAnswer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private Long questionId;

    private String content;

    private DatingExamAnswer(Long questionId, String content) {
        setQuestionId(questionId);
        setContent(content);
    }

    public static DatingExamAnswer create(Long questionId, String content) {
        return new DatingExamAnswer(questionId, content);
    }

    private void setQuestionId(@NonNull Long questionId) {
        this.questionId = questionId;
    }

    private void setContent(@NonNull String content) {
        if (content.isBlank()) {
            throw new InvalidDatingExamAnswerContentException("Content cannot be null or blank");
        }
        this.content = content;
    }
}
