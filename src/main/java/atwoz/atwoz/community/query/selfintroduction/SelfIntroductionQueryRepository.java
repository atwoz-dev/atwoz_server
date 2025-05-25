package atwoz.atwoz.community.query.selfintroduction;

import atwoz.atwoz.community.presentation.selfintroduction.dto.AdminSelfIntroductionSearchCondition;
import atwoz.atwoz.community.presentation.selfintroduction.dto.SelfIntroductionSearchCondition;
import atwoz.atwoz.community.query.selfintroduction.view.*;
import atwoz.atwoz.member.command.domain.member.Hobby;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static atwoz.atwoz.community.command.domain.selfintroduction.QSelfIntroduction.selfIntroduction;
import static atwoz.atwoz.like.command.domain.QLike.like;
import static atwoz.atwoz.member.command.domain.member.QMember.member;
import static atwoz.atwoz.member.command.domain.profileImage.QProfileImage.profileImage;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.set;
import static com.querydsl.core.types.dsl.Expressions.enumPath;

@Repository
@RequiredArgsConstructor
public class SelfIntroductionQueryRepository {
    private static final int PAGE_SIZE = 10;
    private final JPAQueryFactory queryFactory;

    public List<SelfIntroductionSummaryView> findSelfIntroductions(SelfIntroductionSearchCondition searchCondition,
        Long lastId) {

        BooleanExpression condition = getSearchCondition(searchCondition, lastId);

        return queryFactory
            .select(
                new QSelfIntroductionSummaryView(selfIntroduction.id, member.profile.nickname.value,
                    profileImage.imageUrl.value, member.profile.yearOfBirth.value, selfIntroduction.title)
            )
            .from(selfIntroduction)
            .join(member).on(member.id.eq(selfIntroduction.memberId))
            .join(profileImage).on(profileImage.memberId.eq(member.id).and(profileImage.isPrimary.eq(true)))
            .where(condition)
            .limit(PAGE_SIZE)
            .orderBy(selfIntroduction.id.desc())
            .fetch();
    }

    public Optional<SelfIntroductionView> findSelfIntroductionByIdWithMemberId(Long id, Long memberId) {
        EnumPath<Hobby> hobby = enumPath(Hobby.class, "hobbyAlias");

        Map<Long, SelfIntroductionView> view = queryFactory
            .from(selfIntroduction)
            .leftJoin(member).on(member.id.eq(selfIntroduction.memberId))
            .leftJoin(like).on(like.senderId.eq(memberId).and(like.receiverId.eq(member.id)))
            .leftJoin(profileImage).on(profileImage.memberId.eq(member.id).and(profileImage.isPrimary.eq(true)))
            .leftJoin(member.profile.hobbies, hobby)
            .where(selfIntroduction.id.eq(id).and(selfIntroduction.deletedAt.isNull()))
            .transform(
                groupBy(member.id).as(
                    new QSelfIntroductionView(
                        member.id,
                        member.profile.nickname.value,
                        member.profile.yearOfBirth.value,
                        profileImage.imageUrl.value,
                        member.profile.region.city.stringValue(),
                        member.profile.region.district.stringValue(),
                        member.profile.mbti.stringValue(),
                        set(hobby.stringValue()),
                        like.level.stringValue(),
                        selfIntroduction.title,
                        selfIntroduction.content
                    )
                )
            );

        return view.values().stream().findFirst();
    }

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

    private BooleanExpression getSearchCondition(SelfIntroductionSearchCondition searchCondition, Long lastId) {
        BooleanExpression condition = selfIntroduction.deletedAt.isNull();
        if (lastId != null) {
            condition = condition.and(selfIntroduction.id.lt(lastId));
        }
        condition = addYearOfBirthCondition(condition, searchCondition);
        condition = addGenderCondition(condition, searchCondition);
        condition = addPreferredCityCondition(condition, searchCondition);
        return condition == null ? member.isNotNull() : condition;
    }

    private BooleanExpression addYearOfBirthCondition(BooleanExpression condition,
        SelfIntroductionSearchCondition searchCondition) {
        if (searchCondition.fromYearOfBirth() != null && searchCondition.toYearOfBirth() != null) {
            condition =
                (condition == null) ? member.profile.yearOfBirth.value.between(searchCondition.fromYearOfBirth(),
                    searchCondition.toYearOfBirth())
                    : condition.and(member.profile.yearOfBirth.value.between(searchCondition.fromYearOfBirth(),
                        searchCondition.toYearOfBirth()));
        } else if (searchCondition.fromYearOfBirth() != null) {
            condition = (condition == null) ? member.profile.yearOfBirth.value.goe(searchCondition.fromYearOfBirth())
                : condition.and(member.profile.yearOfBirth.value.goe(searchCondition.fromYearOfBirth()));
        } else if (searchCondition.toYearOfBirth() != null) {
            condition = (condition == null) ? member.profile.yearOfBirth.value.loe(searchCondition.toYearOfBirth())
                : condition.and(member.profile.yearOfBirth.value.loe(searchCondition.toYearOfBirth()));
        }
        return condition;
    }

    private BooleanExpression addGenderCondition(BooleanExpression condition,
        SelfIntroductionSearchCondition searchCondition) {
        if (searchCondition.gender() != null) {
            condition = (condition == null) ? member.profile.gender.eq(searchCondition.gender())
                : condition.and(member.profile.gender.eq(searchCondition.gender()));
        }
        return condition;
    }

    private BooleanExpression addPreferredCityCondition(BooleanExpression condition,
        SelfIntroductionSearchCondition searchCondition) {
        if (searchCondition.preferredCities() != null && !searchCondition.preferredCities().isEmpty()) {
            condition = (condition == null) ? member.profile.region.city.in(searchCondition.preferredCities())
                : condition.and(member.profile.region.city.in(searchCondition.preferredCities()));
        }
        return condition;
    }
}
