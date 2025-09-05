package atwoz.atwoz.datingexam.adapter.database;

import atwoz.atwoz.datingexam.application.required.SoulmateQueryRepository;
import atwoz.atwoz.datingexam.domain.DatingExamSubmit;
import atwoz.atwoz.member.command.domain.member.ActivityStatus;
import atwoz.atwoz.member.command.domain.member.Gender;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import static atwoz.atwoz.datingexam.domain.QDatingExamSubmit.datingExamSubmit;
import static atwoz.atwoz.member.command.domain.member.QMember.member;

@Repository
@RequiredArgsConstructor
public class SoulmateQueryRepositoryImpl implements SoulmateQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Set<Long> findSameAnswerMemberIds(Long memberId) {
        Set<Long> equalAnswerMemberIds = getEqualAnswerMemberIds(memberId);

        Gender gender = queryFactory
            .select(member.profile.gender)
            .from(member)
            .where(member.id.eq(memberId))
            .fetchOne();

        if (gender == null) {
            throw new IllegalStateException("멤버의 성별이 null입니다. 멤버 ID: " + memberId);
        }

        return queryFactory
            .select(member.id)
            .from(member)
            .where(member.id.in(equalAnswerMemberIds)
                .and(member.profile.gender.eq(gender.getOpposite()))
                .and(member.isProfilePublic.isTrue())
                .and(member.activityStatus.eq(ActivityStatus.ACTIVE))
                .and(member.id.ne(memberId))
            )
            .orderBy(member.id.desc())
            .fetch()
            .stream()
            .collect(Collectors.toSet());
    }

    private Set<Long> getEqualAnswerMemberIds(Long memberId) {
        List<DatingExamSubmit> submits = queryFactory.select(datingExamSubmit)
            .from(datingExamSubmit)
            .where(datingExamSubmit.memberId.eq(memberId))
            .fetch();

        if (submits.isEmpty()) {
            throw new IllegalStateException("연애 모의고사 제출 기록이 없습니다. memberId: " + memberId);
        }

        List<Set<Long>> equalAnswerMemberIdSets = submits.stream().map(submit -> {
            return new HashSet<>(queryFactory
                .select(datingExamSubmit.memberId)
                .from(datingExamSubmit)
                .where(datingExamSubmit.subjectId.eq(submit.getSubjectId())
                    .and(datingExamSubmit.answers.eq(submit.getAnswers()))
                )
                .fetch())
                .stream()
                .collect(Collectors.toSet());
        }).toList();

        return equalAnswerMemberIdSets.stream()
            .sorted(Comparator.comparingInt(Set::size))
            .reduce((a, b) -> {
                a.retainAll(b);
                return a;
            })
            .orElseGet(Collections::emptySet);
    }
}
