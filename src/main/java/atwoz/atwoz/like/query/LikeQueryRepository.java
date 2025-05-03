package atwoz.atwoz.like.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static atwoz.atwoz.like.command.domain.QLike.like;
import static atwoz.atwoz.member.command.domain.member.QMember.member;
import static atwoz.atwoz.member.command.domain.profileImage.QProfileImage.profileImage;

@Repository
@RequiredArgsConstructor
public class LikeQueryRepository {
    private static final int PAGE_SIZE = 12;
    private final JPAQueryFactory queryFactory;

    public List<LikeView> findSentLikes(long memberId, Long lastId) {
        return queryFactory
            .select(new QLikeView(
                like.id,
                profileImage.imageUrl.value,
                member.profile.nickname.value,
                member.profile.region.city.stringValue(),
                member.profile.yearOfBirth.value,
                like.createdAt
            ))
            .from(like)
            .join(member).on(member.id.eq(like.receiverId))
            .join(profileImage).on(profileImage.memberId.eq(member.id).and(profileImage.isPrimary.eq(true)))
            .where(eqSender(memberId), ltLikeId(lastId))
            .orderBy(like.createdAt.desc())
            .limit(PAGE_SIZE)
            .fetch();
    }

    public List<LikeView> findReceivedLikes(long memberId, Long lastId) {
        return queryFactory
            .select(new QLikeView(
                like.id,
                profileImage.imageUrl.value,
                member.profile.nickname.value,
                member.profile.region.city.stringValue(),
                member.profile.yearOfBirth.value,
                like.createdAt
            ))
            .from(like)
            .join(member).on(member.id.eq(like.senderId))
            .join(profileImage).on(profileImage.memberId.eq(member.id).and(profileImage.isPrimary.eq(true)))
            .where(eqReceiver(memberId), ltLikeId(lastId))
            .orderBy(like.createdAt.desc())
            .limit(PAGE_SIZE)
            .fetch();
    }

    private BooleanExpression eqSender(long memberId) {
        return like.senderId.eq(memberId);
    }

    private BooleanExpression eqReceiver(long memberId) {
        return like.receiverId.eq(memberId);
    }

    private BooleanExpression ltLikeId(Long lastId) {
        return lastId != null ? like.id.lt(lastId) : null;
    }
}
