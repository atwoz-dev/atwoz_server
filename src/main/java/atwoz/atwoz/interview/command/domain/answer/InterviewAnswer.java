package atwoz.atwoz.interview.command.domain.answer;

import atwoz.atwoz.common.entity.BaseEntity;
import atwoz.atwoz.interview.command.application.answer.FirstInterviewSubmittedEvent;
import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.interview.command.domain.answer.exception.InvalidInterviewAnswerContentException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InterviewAnswer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long questionId;

    private Long memberId;

    private String content;

    public static InterviewAnswer of(Long questionId, Long memberId, String content) {
        return new InterviewAnswer(questionId, memberId, content);
    }

    public void submit(boolean hasInterviewAnswer) {
        if (hasInterviewAnswer) {
            return;
        }
        Events.raise(new FirstInterviewSubmittedEvent(memberId));
    }

    private InterviewAnswer(Long questionId, Long memberId, String content) {
        setQuestionId(questionId);
        setMemberId(memberId);
        setContent(content);
    }

    private void setQuestionId(@NonNull Long questionId) {
        this.questionId = questionId;
    }

    private void setMemberId(@NonNull Long memberId) {
        this.memberId = memberId;
    }

    private void setContent(@NonNull String content) {
        if (content.isBlank()) {
            throw new InvalidInterviewAnswerContentException();
        }
        this.content = content;
    }
}
