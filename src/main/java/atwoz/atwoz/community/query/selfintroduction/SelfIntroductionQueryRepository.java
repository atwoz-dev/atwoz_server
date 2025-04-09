package atwoz.atwoz.community.query.selfintroduction;

import atwoz.atwoz.community.query.selfintroduction.view.QSelfIntroductionSummaryView;
import atwoz.atwoz.community.query.selfintroduction.view.QSelfIntroductionView;
import atwoz.atwoz.community.query.selfintroduction.view.SelfIntroductionSummaryView;
import atwoz.atwoz.community.query.selfintroduction.view.SelfIntroductionView;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static atwoz.atwoz.admin.command.domain.hobby.QHobby.hobby;
import static atwoz.atwoz.community.command.domain.selfintroduction.QSelfIntroduction.selfIntroduction;
import static atwoz.atwoz.like.command.domain.like.QLike.like;
import static atwoz.atwoz.member.command.domain.member.QMember.member;
import static atwoz.atwoz.member.command.domain.profileImage.QProfileImage.profileImage;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

@Repository
@RequiredArgsConstructor
public class SelfIntroductionQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Page<SelfIntroductionSummaryView> findSelfIntroductions(SelfIntroductionSearchCondition searchCondition, Pageable pageable) {

        BooleanExpression condition = getSearchCondition(searchCondition);

        List<SelfIntroductionSummaryView> content = queryFactory
                .select(
                        new QSelfIntroductionSummaryView(selfIntroduction.id, member.profile.nickname.value, profileImage.imageUrl.value, member.profile.yearOfBirth.value, selfIntroduction.title)
                )
                .from(selfIntroduction)
                .join(member).on(member.id.eq(selfIntroduction.memberId))
                .join(profileImage).on(profileImage.memberId.eq(member.id).and(profileImage.isPrimary.eq(true)))
                .where(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(selfIntroduction.id.desc())
                .fetch();

        long totalCount = Optional.ofNullable(
                queryFactory
                        .select(selfIntroduction.count())
                        .from(selfIntroduction)
                        .join(member).on(member.id.eq(selfIntroduction.memberId))
                        .join(profileImage).on(profileImage.memberId.eq(member.id).and(profileImage.isPrimary.eq(true)))
                        .where(condition)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(content, pageable, totalCount);
    }

    public Optional<SelfIntroductionView> findSelfIntroductionByIdWithMemberId(Long id, Long memberId) {
        Map<Long, SelfIntroductionView> view = queryFactory
                .from(selfIntroduction)
                .leftJoin(member).on(member.id.eq(selfIntroduction.memberId))
                .leftJoin(like).on(like.senderId.eq(memberId).and(like.receiverId.eq(member.id)))
                .leftJoin(profileImage).on(profileImage.memberId.eq(member.id).and(profileImage.isPrimary.eq(true)))
                .leftJoin(hobby).on(hobby.id.in(member.profile.hobbyIds))
                .where(selfIntroduction.id.eq(id))
                .transform(
                        groupBy(member.id).as(
                                new QSelfIntroductionView(
                                        member.id,
                                        member.profile.nickname.value,
                                        member.profile.yearOfBirth.value,
                                        profileImage.imageUrl.value,
                                        member.profile.region.stringValue(),
                                        member.profile.mbti.stringValue(),
                                        list(hobby.name),
                                        like.likeLevel.stringValue(),
                                        selfIntroduction.title,
                                        selfIntroduction.content
                                )
                        )
                );

        return view.values().stream().findFirst();
    }

    private BooleanExpression getSearchCondition(SelfIntroductionSearchCondition searchCondition) {
        BooleanExpression condition = addYearOfBirthCondition(null, searchCondition);
        condition = addGenderCondition(condition, searchCondition);
        condition = addPreferredRegionCondition(condition, searchCondition);
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

    private BooleanExpression addPreferredRegionCondition(BooleanExpression condition, SelfIntroductionSearchCondition searchCondition) {
        if (searchCondition.preferredRegions() != null && !searchCondition.preferredRegions().isEmpty()) {
            condition = (condition == null) ? member.profile.region.in(searchCondition.preferredRegions())
                    : condition.and(member.profile.region.in(searchCondition.preferredRegions()));
        }
        return condition;
    }
}
