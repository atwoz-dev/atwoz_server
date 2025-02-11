package atwoz.atwoz.interview.command.domain.question;

import atwoz.atwoz.common.entity.BaseEntity;
import atwoz.atwoz.interview.command.domain.question.exception.InvalidInterviewQuestionContentException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InterviewQuestion extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private InterviewCategory category;

    private boolean isPublic;

    public static InterviewQuestion of(String content, InterviewCategory category, boolean isPublic) {
        return new InterviewQuestion(content, category, isPublic);
    }

    private InterviewQuestion(String content, InterviewCategory category, boolean isPublic) {
        setContent(content);
        setCategory(category);
        this.isPublic = isPublic;
    }

    private void setContent(@NonNull String content) {
        if (content.isBlank()) {
            throw new InvalidInterviewQuestionContentException();
        }
        this.content = content;
    }

    private void setCategory(@NonNull InterviewCategory category) {
        this.category = category;
    }
}
