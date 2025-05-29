package atwoz.atwoz.community.query.selfintroduction;

import atwoz.atwoz.community.presentation.selfintroduction.dto.SelfIntroductionSearchCondition;
import atwoz.atwoz.community.query.selfintroduction.view.QSelfIntroductionSummaryView;
import atwoz.atwoz.community.query.selfintroduction.view.QSelfIntroductionView;
import atwoz.atwoz.community.query.selfintroduction.view.SelfIntroductionSummaryView;
import atwoz.atwoz.community.query.selfintroduction.view.SelfIntroductionView;
import atwoz.atwoz.member.command.domain.member.Hobby;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
