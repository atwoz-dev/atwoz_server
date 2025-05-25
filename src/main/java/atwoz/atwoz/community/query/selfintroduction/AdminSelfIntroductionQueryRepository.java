package atwoz.atwoz.community.query.selfintroduction;

import atwoz.atwoz.community.presentation.selfintroduction.dto.AdminSelfIntroductionSearchCondition;
import atwoz.atwoz.community.query.selfintroduction.view.AdminSelfIntroductionView;
import atwoz.atwoz.community.query.selfintroduction.view.QAdminSelfIntroductionView;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static atwoz.atwoz.community.command.domain.selfintroduction.QSelfIntroduction.selfIntroduction;
import static atwoz.atwoz.member.command.domain.member.QMember.member;

@Repository
@RequiredArgsConstructor
public class AdminSelfIntroductionQueryRepository {

    private final JPQLQueryFactory queryFactory;

    public Page<AdminSelfIntroductionView> findSelfIntroductions(AdminSelfIntroductionSearchCondition condition,
        Pageable pageable) {
        List<AdminSelfIntroductionView> content = queryFactory
            .select(
                new QAdminSelfIntroductionView(
                    selfIntroduction.id,
                    member.profile.nickname.value,
                    member.profile.gender.stringValue(),
                    selfIntroduction.isOpened,
                    selfIntroduction.content,
                    selfIntroduction.createdAt,
                    selfIntroduction.updatedAt,
                    selfIntroduction.deletedAt
                )
            )
            .from(selfIntroduction)
            .join(member).on(member.id.eq(selfIntroduction.memberId))
            .where(
                nicknameEq(condition.nickname()),
                isOpenedEq(condition.isOpened()),
                startDateGoe(condition.startDate()),
                loeEndDate(condition.endDate()),
                phoneNumberEq(condition.phoneNumber())
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long totalCount = Optional.ofNullable(
            queryFactory
                .select(selfIntroduction.count())
                .from(selfIntroduction)
                .join(member).on(member.id.eq(selfIntroduction.memberId))
                .where(
                    nicknameEq(condition.nickname()),
                    isOpenedEq(condition.isOpened()),
                    startDateGoe(condition.startDate()),
                    loeEndDate(condition.endDate()),
                    phoneNumberEq(condition.phoneNumber())
                )
                .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(content, pageable, totalCount);
    }

    private BooleanExpression nicknameEq(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            return null;
        }
        return member.profile.nickname.value.eq(nickname);
    }

    private BooleanExpression isOpenedEq(Boolean isOpened) {
        if (isOpened == null) {
            return null;
        }
        return selfIntroduction.isOpened.eq(isOpened);
    }

    private BooleanExpression startDateGoe(LocalDate startDate) {
        if (startDate == null) {
            return null;
        }
        return selfIntroduction.createdAt.goe(startDate.atStartOfDay());
    }

    private BooleanExpression loeEndDate(LocalDate endDate) {
        if (endDate == null) {
            return null;
        }
        return selfIntroduction.createdAt.loe(endDate.plusDays(1).atStartOfDay().minusSeconds(1));
    }

    private BooleanExpression phoneNumberEq(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return null;
        }
        return member.phoneNumber.value.eq(phoneNumber);
    }
}
