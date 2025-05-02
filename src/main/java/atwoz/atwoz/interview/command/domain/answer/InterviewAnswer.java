package atwoz.atwoz.interview.command.domain.answer;

import atwoz.atwoz.common.entity.BaseEntity;
import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.interview.command.domain.answer.event.FirstInterviewSubmittedEvent;
import atwoz.atwoz.interview.command.domain.answer.exception.InvalidInterviewAnswerContentException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "interview_answers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InterviewAnswer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    private Long questionId;

    private Long memberId;

    @Getter
    private String content;

    private InterviewAnswer(Long questionId, Long memberId, String content) {
        setQuestionId(questionId);
        setMemberId(memberId);
        setContent(content);
    }

    public static InterviewAnswer of(Long questionId, Long memberId, String content) {
        return new InterviewAnswer(questionId, memberId, content);
    }

    public void submitFirstInterviewAnswer() {
        Events.raise(new FirstInterviewSubmittedEvent(memberId));
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
