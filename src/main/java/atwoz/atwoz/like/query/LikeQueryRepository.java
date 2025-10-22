package atwoz.atwoz.like.query;

import atwoz.atwoz.like.command.domain.QLike;
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
    private static final int PAGE_SIZE = 13;
    private final JPAQueryFactory queryFactory;

    public List<RawLikeView> findSentLikes(long senderId, Long lastLikeId) {
        QLike mutual = new QLike("mutual");

        return queryFactory
            .select(new QRawLikeView(
                like.id,
                like.receiverId,
                profileImage.imageUrl.value,
                member.profile.nickname.value,
                member.profile.region.city.stringValue(),
                member.profile.yearOfBirth.value,
                mutual.id.isNotNull(),
                like.createdAt
            ))
            .from(like)
            .join(member).on(member.id.eq(like.receiverId))
            .leftJoin(profileImage).on(profileImage.memberId.eq(member.id).and(profileImage.isPrimary.eq(true)))
            .leftJoin(mutual).on(mutual.senderId.eq(like.receiverId).and(mutual.receiverId.eq(senderId)))
            .where(eqSender(senderId), ltLikeId(lastLikeId))
            .orderBy(like.createdAt.desc(), like.id.desc())
            .limit(PAGE_SIZE)
            .fetch();
    }

    public List<RawLikeView> findReceivedLikes(long receiverId, Long lastLikeId) {
        QLike mutual = new QLike("mutual");

        return queryFactory
            .select(new QRawLikeView(
                like.id,
                like.senderId,
                profileImage.imageUrl.value,
                member.profile.nickname.value,
                member.profile.region.city.stringValue(),
                member.profile.yearOfBirth.value,
                mutual.id.isNotNull(),
                like.createdAt
            ))
            .from(like)
            .join(member).on(member.id.eq(like.senderId))
            .leftJoin(profileImage).on(profileImage.memberId.eq(member.id).and(profileImage.isPrimary.eq(true)))
            .leftJoin(mutual).on(mutual.senderId.eq(receiverId).and(mutual.receiverId.eq(like.senderId)))
            .where(eqReceiver(receiverId), ltLikeId(lastLikeId))
            .orderBy(like.createdAt.desc(), like.id.desc())
            .limit(PAGE_SIZE)
            .fetch();
    }

    private BooleanExpression eqSender(long senderId) {
        return like.senderId.eq(senderId);
    }

    private BooleanExpression eqReceiver(long receiverId) {
        return like.receiverId.eq(receiverId);
    }

    private BooleanExpression ltLikeId(Long lastLikeId) {
        return lastLikeId != null ? like.id.lt(lastLikeId) : null;
    }
}
