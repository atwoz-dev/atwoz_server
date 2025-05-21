package atwoz.atwoz.interview.query.question;

import atwoz.atwoz.interview.command.domain.question.InterviewCategory;
import atwoz.atwoz.interview.query.question.view.InterviewQuestionView;
import atwoz.atwoz.interview.query.question.view.QInterviewQuestionView;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static atwoz.atwoz.interview.command.domain.answer.QInterviewAnswer.interviewAnswer;
import static atwoz.atwoz.interview.command.domain.question.QInterviewQuestion.interviewQuestion;

@Repository
@RequiredArgsConstructor
public class InterviewQuestionQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<InterviewQuestionView> findAllQuestionByCategoryWithMemberId(
        String category,
        Long memberId
    ) {
        return queryFactory
            .select(new QInterviewQuestionView(
                interviewQuestion.id,
                interviewQuestion.content,
                interviewQuestion.category.stringValue(),
                interviewAnswer.id.isNotNull(),
                interviewAnswer.id,
                interviewAnswer.content
            ))
            .from(interviewQuestion)
            .leftJoin(interviewAnswer)
            .on(interviewAnswer.questionId.eq(interviewQuestion.id).and(interviewAnswer.memberId.eq(memberId)))
            .where(
                categoryEq(category),
                isPublic()
            )
            .fetch();
    }

    public Optional<InterviewQuestionView> findQuestionByIdWithMemberId(Long questionId, Long memberId) {
        return Optional.ofNullable(
            queryFactory
                .select(new QInterviewQuestionView(
                    interviewQuestion.id,
                    interviewQuestion.content,
                    interviewQuestion.category.stringValue(),
                    interviewAnswer.id.isNotNull(),
                    interviewAnswer.id,
                    interviewAnswer.content
                ))
                .from(interviewQuestion)
                .leftJoin(interviewAnswer)
                .on(interviewAnswer.questionId.eq(interviewQuestion.id).and(interviewAnswer.memberId.eq(memberId)))
                .where(
                    idEq(questionId),
                    isPublic()
                )
                .fetchOne()
        );
    }

    private BooleanExpression categoryEq(String category) {
        if (category == null) {
            return null;
        }
        return interviewQuestion.category.eq(InterviewCategory.from(category));
    }

    private BooleanExpression idEq(final long id) {
        return interviewQuestion.id.eq(id);
    }

    private BooleanExpression isPublic() {
        return interviewQuestion.isPublic.isTrue();
    }
}
