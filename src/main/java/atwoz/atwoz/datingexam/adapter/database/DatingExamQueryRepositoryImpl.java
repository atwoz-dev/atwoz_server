package atwoz.atwoz.datingexam.adapter.database;

import atwoz.atwoz.datingexam.application.dto.DatingExamAnswerInfo;
import atwoz.atwoz.datingexam.application.dto.DatingExamInfoResponse;
import atwoz.atwoz.datingexam.application.dto.DatingExamQuestionInfo;
import atwoz.atwoz.datingexam.application.dto.DatingExamSubjectInfo;
import atwoz.atwoz.datingexam.application.required.DatingExamQueryRepository;
import atwoz.atwoz.datingexam.domain.SubjectType;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.*;

import static atwoz.atwoz.datingexam.domain.QDatingExamAnswer.datingExamAnswer;
import static atwoz.atwoz.datingexam.domain.QDatingExamQuestion.datingExamQuestion;
import static atwoz.atwoz.datingexam.domain.QDatingExamSubject.datingExamSubject;

@Repository
@RequiredArgsConstructor
public class DatingExamQueryRepositoryImpl implements DatingExamQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    @Cacheable(value = "datingExamInfo", key = "#subjectType")
    public DatingExamInfoResponse findDatingExamInfo(SubjectType subjectType) {
        Map<Long, DatingExamSubjectInfo> subjectInfoMap = fetchSubjects(subjectType);
        if (subjectInfoMap.isEmpty()) {
            return new DatingExamInfoResponse(Collections.emptyList());
        }

        Map<Long, DatingExamQuestionInfo> questionInfoMap = fetchAndAttachQuestions(subjectInfoMap);
        attachAnswersToQuestions(questionInfoMap);

        return new DatingExamInfoResponse(new ArrayList<>(subjectInfoMap.values()));
    }

    private Map<Long, DatingExamSubjectInfo> fetchSubjects(SubjectType subjectType) {
        List<Tuple> subjectTuples = queryFactory
            .select(
                datingExamSubject.id,
                datingExamSubject.type.stringValue(),
                datingExamSubject.name
            )
            .from(datingExamSubject)
            .where(datingExamSubject.type.eq(subjectType))
            .orderBy(datingExamSubject.id.asc())
            .fetch();

        Map<Long, DatingExamSubjectInfo> subjectInfoMap = new LinkedHashMap<>();
        for (Tuple row : subjectTuples) {
            Long id = row.get(datingExamSubject.id);
            String type = row.get(datingExamSubject.type.stringValue());
            String name = row.get(datingExamSubject.name);

            DatingExamSubjectInfo subjectInfo =
                new DatingExamSubjectInfo(id, type, name, new ArrayList<>());

            subjectInfoMap.put(id, subjectInfo);
        }
        return subjectInfoMap;
    }

    private Map<Long, DatingExamQuestionInfo> fetchAndAttachQuestions(
        Map<Long, DatingExamSubjectInfo> subjectInfoMap
    ) {
        Set<Long> subjectIds = subjectInfoMap.keySet();
        Map<Long, DatingExamQuestionInfo> questionInfoMap = new LinkedHashMap<>();

        List<Tuple> questionTuples = queryFactory
            .select(
                datingExamQuestion.subjectId,
                datingExamQuestion.id,
                datingExamQuestion.content
            )
            .from(datingExamQuestion)
            .where(datingExamQuestion.subjectId.in(subjectIds))
            .orderBy(datingExamQuestion.subjectId.asc(), datingExamQuestion.id.asc())
            .fetch();

        for (Tuple questionTuple : questionTuples) {
            Long subjectId = questionTuple.get(datingExamQuestion.subjectId);
            Long questionId = questionTuple.get(datingExamQuestion.id);
            String questionContent = questionTuple.get(datingExamQuestion.content);

            DatingExamQuestionInfo questionInfo = new DatingExamQuestionInfo(
                questionId,
                questionContent,
                new ArrayList<>()
            );
            subjectInfoMap.get(subjectId).questions().add(questionInfo);
            questionInfoMap.put(questionId, questionInfo);
        }

        return questionInfoMap;
    }

    private void attachAnswersToQuestions(Map<Long, DatingExamQuestionInfo> questionInfoMap) {
        Set<Long> questionIds = questionInfoMap.keySet();

        List<Tuple> answerTuples = queryFactory
            .select(
                datingExamAnswer.questionId,
                datingExamAnswer.id,
                datingExamAnswer.content
            )
            .from(datingExamAnswer)
            .where(datingExamAnswer.questionId.in(questionIds))
            .orderBy(datingExamAnswer.questionId.asc(), datingExamAnswer.id.asc())
            .fetch();

        for (Tuple answerTuple : answerTuples) {
            Long questionId = answerTuple.get(datingExamAnswer.questionId);
            Long answerId = answerTuple.get(datingExamAnswer.id);
            String answerContent = answerTuple.get(datingExamAnswer.content);

            DatingExamAnswerInfo answerInfo = new DatingExamAnswerInfo(answerId, answerContent);
            questionInfoMap.get(questionId).answers().add(answerInfo);
        }
    }
}
