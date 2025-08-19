package atwoz.atwoz.datingexam.adapter.database;

import atwoz.atwoz.datingexam.application.required.SoulmateQueryRepository;
import atwoz.atwoz.member.command.domain.member.ActivityStatus;
import atwoz.atwoz.member.command.domain.member.Gender;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.stream.Collectors;

import static atwoz.atwoz.datingexam.domain.QDatingExamSubmit.datingExamSubmit;
import static atwoz.atwoz.member.command.domain.member.QMember.member;

@Repository
@RequiredArgsConstructor
public class SoulmateQueryRepositoryImpl implements SoulmateQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Set<Long> findSoulmateIds(Long memberId, String requiredSubjectAnswers) {
        Gender gender = queryFactory
            .select(member.profile.gender)
            .from(member)
            .where(member.id.eq(memberId))
            .fetchOne();

        return queryFactory
            .select(datingExamSubmit.memberId)
            .from(datingExamSubmit)
            .innerJoin(member).on(member.id.eq(datingExamSubmit.memberId))
            .where(datingExamSubmit.requiredSubjectAnswers.eq(requiredSubjectAnswers)
                .and(member.profile.gender.eq(gender.getOpposite()))
                .and(member.isProfilePublic.isTrue())
                .and(member.activityStatus.eq(ActivityStatus.ACTIVE))
                .and(datingExamSubmit.memberId.ne(memberId))
            )
            .orderBy(member.id.desc())
            .fetch()
            .stream()
            .collect(Collectors.toSet());
    }
}
