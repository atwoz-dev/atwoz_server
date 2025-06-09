package atwoz.atwoz.admin.query.member;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static atwoz.atwoz.admin.command.domain.warning.QWarning.warning;
import static atwoz.atwoz.member.command.domain.member.QMember.member;

@Repository
@RequiredArgsConstructor
public class AdminMemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<MemberView> findMembers(MemberSearchCondition condition, Pageable pageable) {
        List<MemberView> views = queryFactory
            .select(new QMemberView(
                member.id,
                member.profile.nickname.value,
                member.profile.gender.stringValue(),
                member.activityStatus.stringValue(),
                member.createdAt,
                warning.count().intValue()
            ))
            .from(member)
            .leftJoin(warning).on(warning.memberId.eq(member.id))
            .where(buildFindMembersFilterExpression(condition))
            .groupBy(member.id)
            .orderBy(member.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long totalCount = queryFactory
            .select(member.count())
            .from(member)
            .where(buildFindMembersFilterExpression(condition))
            .fetchOne();

        return new PageImpl<>(views, pageable, totalCount);
    }

    private BooleanExpression buildFindMembersFilterExpression(MemberSearchCondition condition) {
        return Expressions.allOf(
            activityStatusEq(condition.activityStatus()),
            genderEq(condition.gender()),
            gradeEq(condition.grade()),
            nicknameContains(condition.nickname()),
            phoneNumberEq(condition.phoneNumber()),
            createdAtGoe(condition.startDate()),
            createdAtLoe(condition.endDate())
        );
    }

    private BooleanExpression activityStatusEq(String activityStatus) {
        return activityStatus != null ? member.activityStatus.stringValue().eq(activityStatus) : null;
    }

    private BooleanExpression genderEq(String gender) {
        return gender != null ? member.profile.gender.stringValue().eq(gender) : null;
    }

    private BooleanExpression gradeEq(String grade) {
        return grade != null ? member.grade.stringValue().eq(grade) : null;
    }

    private BooleanExpression nicknameContains(String nickname) {
        return nickname != null ? member.profile.nickname.value.contains(nickname) : null;
    }

    private BooleanExpression phoneNumberEq(String phoneNumber) {
        return phoneNumber != null ? member.phoneNumber.value.eq(phoneNumber) : null;
    }

    private BooleanExpression createdAtGoe(LocalDate createdDateGoe) {
        return createdDateGoe != null ? member.createdAt.goe(createdDateGoe.atStartOfDay()) : null;
    }

    private BooleanExpression createdAtLoe(LocalDate createdDateLoe) {
        return createdDateLoe != null ? member.createdAt.lt(createdDateLoe.plusDays(1).atStartOfDay()) : null;
    }

    public MemberDetailView findById(long memberId) {
        return null;
    }
}
