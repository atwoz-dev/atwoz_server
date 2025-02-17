package atwoz.atwoz.admin.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static atwoz.atwoz.admin.command.domain.memberscreening.QMemberScreening.memberScreening;
import static atwoz.atwoz.member.command.domain.member.QMember.member;

@Repository
@RequiredArgsConstructor
public class ScreeningMemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<ScreeningMemberView> findScreeningMembers(ScreeningSearchCondition condition, Pageable pageable) {
        List<ScreeningMemberView> content = queryFactory
                .select(new QScreeningMemberView(
                        memberScreening.id,
                        member.profile.nickname.value,
                        member.profile.gender.stringValue(),
                        member.createdAt.stringValue(),
                        memberScreening.status.stringValue(),
                        memberScreening.rejectionReason.stringValue()
                ))
                .from(memberScreening)
                .join(member).on(member.id.eq(memberScreening.id))
                .where(
                        screeningStatusEq(condition.screeningStatus()),
                        nicknameEq(condition.nickname()),
                        phoneNumberEq(condition.phoneNumber()),
                        startDateGoe(condition.startDate()),
                        loeEndDate(condition.endDate())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalCount = Optional.ofNullable(
                queryFactory
                        .select(memberScreening.count())
                        .from(memberScreening)
                        .join(member).on(member.id.eq(memberScreening.id))
                        .where(
                                screeningStatusEq(condition.screeningStatus()),
                                nicknameEq(condition.nickname()),
                                phoneNumberEq(condition.phoneNumber()),
                                startDateGoe(condition.startDate()),
                                loeEndDate(condition.endDate())
                        )
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(content, pageable, totalCount);
    }

    private BooleanExpression screeningStatusEq(String screeningStatus) {
        if (screeningStatus == null) {
            return null;
        }
        return memberScreening.status.stringValue().eq(screeningStatus);
    }

    private BooleanExpression nicknameEq(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            return null;
        }
        return member.profile.nickname.value.eq(nickname);
    }

    private BooleanExpression phoneNumberEq(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return null;
        }
        return member.phoneNumber.value.eq(phoneNumber);
    }

    private BooleanExpression startDateGoe(LocalDate startDate) {
        if (startDate == null) {
            return null;
        }
        return member.createdAt.goe(startDate.atStartOfDay());
    }

    private BooleanExpression loeEndDate(LocalDate endDate) {
        if (endDate == null) {
            return null;
        }
        return member.createdAt.loe(endDate.plusDays(1).atStartOfDay().minusSeconds(1));
    }
}
