package atwoz.atwoz.community.query.selfintroduction;

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
import static atwoz.atwoz.like.command.domain.like.QLike.like;
import static atwoz.atwoz.member.command.domain.member.QMember.member;
import static atwoz.atwoz.member.command.domain.profileImage.QProfileImage.profileImage;
import static com.querydsl.core.group.GroupBy.*;
import static com.querydsl.core.types.dsl.Expressions.enumPath;

@Repository
@RequiredArgsConstructor
public class SelfIntroductionQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final static int PAGE_SIZE = 10;

    /**
     * Retrieves a paginated list of self-introduction summaries matching the specified search conditions.
     *
     * @param searchCondition criteria for filtering self-introductions
     * @param lastId the ID of the last self-introduction from the previous page, used for pagination; may be null
     * @return a list of self-introduction summary views matching the search criteria, limited to a fixed page size
     */
    public List<SelfIntroductionSummaryView> findSelfIntroductions(SelfIntroductionSearchCondition searchCondition, Long lastId) {

        BooleanExpression condition = getSearchCondition(searchCondition, lastId);

        List<SelfIntroductionSummaryView> view = queryFactory
                .select(
                        new QSelfIntroductionSummaryView(selfIntroduction.id, member.profile.nickname.value, profileImage.imageUrl.value, member.profile.yearOfBirth.value, selfIntroduction.title)
                )
                .from(selfIntroduction)
                .join(member).on(member.id.eq(selfIntroduction.memberId))
                .join(profileImage).on(profileImage.memberId.eq(member.id).and(profileImage.isPrimary.eq(true)))
                .where(condition)
                .limit(PAGE_SIZE)
                .orderBy(selfIntroduction.id.desc())
                .fetch();

        return view;
    }

    /**
     * Retrieves detailed self-introduction information for a given self-introduction ID, including member details, like status from a specific member, and the member's hobbies.
     *
     * @param id the ID of the self-introduction to retrieve
     * @param memberId the ID of the member requesting the information, used to determine like status
     * @return an {@code Optional} containing the self-introduction view if found, or empty if not found
     */
    public Optional<SelfIntroductionView> findSelfIntroductionByIdWithMemberId(Long id, Long memberId) {
        EnumPath<Hobby> hobby = enumPath(Hobby.class, "hobbyAlias");

        Map<Long, SelfIntroductionView> view = queryFactory
                .from(selfIntroduction)
                .leftJoin(member).on(member.id.eq(selfIntroduction.memberId))
                .leftJoin(like).on(like.senderId.eq(memberId).and(like.receiverId.eq(member.id)))
                .leftJoin(profileImage).on(profileImage.memberId.eq(member.id).and(profileImage.isPrimary.eq(true)))
                .leftJoin(member.profile.hobbies, hobby)
                .where(selfIntroduction.id.eq(id))
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
                                        like.likeLevel.stringValue(),
                                        selfIntroduction.title,
                                        selfIntroduction.content
                                )
                        )
                );

        return view.values().stream().findFirst();
    }

    private BooleanExpression getSearchCondition(SelfIntroductionSearchCondition searchCondition, Long lastId) {
        BooleanExpression condition = null;
        if (lastId != null) {
            condition = selfIntroduction.id.lt(lastId);
        }
        condition = addYearOfBirthCondition(condition, searchCondition);
        condition = addGenderCondition(condition, searchCondition);
        condition = addPreferredCityCondition(condition, searchCondition);
        return condition == null ? member.isNotNull() : condition;
    }

    private BooleanExpression addYearOfBirthCondition(BooleanExpression condition, SelfIntroductionSearchCondition searchCondition) {
        if (searchCondition.fromYearOfBirth() != null && searchCondition.toYearOfBirth() != null) {
            condition = (condition == null) ? member.profile.yearOfBirth.value.between(searchCondition.fromYearOfBirth(), searchCondition.toYearOfBirth())
                    : condition.and(member.profile.yearOfBirth.value.between(searchCondition.fromYearOfBirth(), searchCondition.toYearOfBirth()));
        } else if (searchCondition.fromYearOfBirth() != null) {
            condition = (condition == null) ? member.profile.yearOfBirth.value.goe(searchCondition.fromYearOfBirth())
                    : condition.and(member.profile.yearOfBirth.value.goe(searchCondition.fromYearOfBirth()));
        } else if (searchCondition.toYearOfBirth() != null) {
            condition = (condition == null) ? member.profile.yearOfBirth.value.loe(searchCondition.toYearOfBirth())
                    : condition.and(member.profile.yearOfBirth.value.loe(searchCondition.toYearOfBirth()));
        }
        return condition;
    }

    private BooleanExpression addGenderCondition(BooleanExpression condition, SelfIntroductionSearchCondition searchCondition) {
        if (searchCondition.gender() != null) {
            condition = (condition == null) ? member.profile.gender.eq(searchCondition.gender())
                    : condition.and(member.profile.gender.eq(searchCondition.gender()));
        }
        return condition;
    }

    private BooleanExpression addPreferredCityCondition(BooleanExpression condition, SelfIntroductionSearchCondition searchCondition) {
        if (searchCondition.preferredCities() != null && !searchCondition.preferredCities().isEmpty()) {
            condition = (condition == null) ? member.profile.region.city.in(searchCondition.preferredCities())
                    : condition.and(member.profile.region.city.in(searchCondition.preferredCities()));
        }
        return condition;
    }
}
