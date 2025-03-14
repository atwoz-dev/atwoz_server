package atwoz.atwoz.member.query.introduction.intra;

import atwoz.atwoz.member.query.introduction.application.IntroductionSearchCondition;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

import static atwoz.atwoz.admin.command.domain.hobby.QHobby.hobby;
import static atwoz.atwoz.interview.command.domain.answer.QInterviewAnswer.interviewAnswer;
import static atwoz.atwoz.match.command.domain.match.QMatch.match;
import static atwoz.atwoz.member.command.domain.introduction.QMemberIntroduction.memberIntroduction;
import static atwoz.atwoz.member.command.domain.member.QMember.member;
import static atwoz.atwoz.member.command.domain.profileImage.QProfileImage.profileImage;

@Repository
@RequiredArgsConstructor
public class IntroductionQueryRepository {
    private final JPAQueryFactory queryFactory;

    private static final Long LIMIT = 10L;

    public Set<Long> findAllMatchRequestedMemberId(long memberId) {
        return new HashSet<>(queryFactory
                .select(match.requesterId)
                .from(match)
                .where(match.responderId.eq(memberId))
                .fetch());
    }

    public Set<Long> findAllMatchRequestingMemberId(long memberId) {
        return new HashSet<>(queryFactory
                .select(match.responderId)
                .from(match)
                .where(match.requesterId.eq(memberId))
                .fetch());
    }

    public Set<Long> findAllIntroducedMemberId(long memberId) {
        return new HashSet<>(queryFactory
                .select(memberIntroduction.introducedMemberId)
                .from(memberIntroduction)
                .where(memberIntroduction.memberId.eq(memberId))
                .fetch());
    }

    public Set<Long> findAllIntroductionMemberId(IntroductionSearchCondition condition) {
        JPAQuery<Long> query = queryFactory
                .select(member.id)
                .from(member)
                .where(
                        idsNotIn(condition.getExcludedMemberIds()),
                        ageBetween(condition.getMinAge(), condition.getMaxAge()),
                        regionEq(condition.getRegion()),
                        religionEq(condition.getReligion()),
                        smokingStatusEq(condition.getSmokingStatus()),
                        drinkingStatusEq(condition.getDrinkingStatus()),
                        gradeEq(condition.getMemberGrade()),
                        genderEq(condition.getGender()),
                        createdAtGoe(condition.getJoinedAfter())
                )
                .orderBy(member.id.desc())
                .limit(LIMIT);

        applyHobbyIdsCondition(query, condition);
        return new HashSet<>(query.fetch());
    }

    public List<MemberIntroductionProfileQueryResult> findAllMemberIntroductionProfileQueryResultByMemberIds(long memberId, Set<Long> memberIds) {
        return new ArrayList<>(queryFactory
                .from(member)
                .leftJoin(hobby).on(hobby.id.in(member.profile.hobbyIds))
                .leftJoin(memberIntroduction).on(memberIntroduction.memberId.eq(memberId))
                .leftJoin(profileImage).on(profileImage.memberId.eq(member.id).and(profileImage.isPrimary.isTrue()))
                .where(member.id.in(memberIds))
                .orderBy(member.id.desc())
                .transform(GroupBy.groupBy(member.id).as(
                        new QMemberIntroductionProfileQueryResult(
                                member.id,
                                profileImage.imageUrl.value,
                                GroupBy.list(hobby.name),
                                member.profile.religion.stringValue(),
                                member.profile.mbti.stringValue(),
                                memberIntroduction.introducedMemberId.isNotNull()
                        )
                )).values());
    }

    public List<InterviewAnswerQueryResult> findAllInterviewAnswerInfoByMemberIds(Set<Long> memberIds) {
        return queryFactory
                .select(new QInterviewAnswerQueryResult(
                        interviewAnswer.memberId,
                        interviewAnswer.content
                ))
                .from(interviewAnswer)
                .where(interviewAnswer.memberId.in(memberIds))
                .orderBy(interviewAnswer.id.asc())
                .fetch();
    }

    private BooleanExpression idsNotIn(Set<Long> id) {
        if (id.isEmpty()) {
            return null;
        }
        return member.id.notIn(id);
    }

    private BooleanExpression ageBetween(Integer minAge, Integer maxAge) {
        if (minAge == null && maxAge == null) {
            return null;
        }
        if (minAge == null) {
            return member.profile.age.loe(maxAge);
        }
        if (maxAge == null) {
            return member.profile.age.goe(minAge);
        }
        return member.profile.age.between(minAge, maxAge);
    }

    private BooleanExpression regionEq(String region) {
        if (region == null) {
            return null;
        }
        return member.profile.region.stringValue().eq(region);
    }

    private BooleanExpression religionEq(String religion) {
        if (religion == null) {
            return null;
        }
        return member.profile.religion.stringValue().eq(religion);
    }

    private BooleanExpression smokingStatusEq(String smokingStatus) {
        if (smokingStatus == null) {
            return null;
        }
        return member.profile.smokingStatus.stringValue().eq(smokingStatus);
    }

    private BooleanExpression drinkingStatusEq(String drinkingStatus) {
        if (drinkingStatus == null) {
            return null;
        }
        return member.profile.drinkingStatus.stringValue().eq(drinkingStatus);
    }

    private BooleanExpression gradeEq(String grade) {
        if (grade == null) {
            return null;
        }
        return member.grade.stringValue().eq(grade);
    }

    private BooleanExpression genderEq(String gender) {
        if (gender == null) {
            return null;
        }
        return member.profile.gender.stringValue().eq(gender);
    }

    private BooleanExpression createdAtGoe(LocalDateTime createdAt) {
        if (createdAt == null) {
            return null;
        }
        return member.createdAt.goe(createdAt);
    }

    private void applyHobbyIdsCondition(JPAQuery<?> query, IntroductionSearchCondition condition) {
        if (condition.getHobbyIds() != null && !condition.getHobbyIds().isEmpty()) {
            NumberPath<Long> hobbyId = Expressions.numberPath(Long.class, "hobbyId");
            query.join(member.profile.hobbyIds, hobbyId)
                    .on(hobbyId.in(condition.getHobbyIds()))
                    .distinct();
        }
    }
}
