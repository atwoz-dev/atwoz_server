package deepple.deepple.match.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static deepple.deepple.block.domain.QBlock.block;
import static deepple.deepple.match.command.domain.match.QMatch.match;
import static deepple.deepple.member.command.domain.member.QMember.member;
import static deepple.deepple.member.command.domain.profileImage.QProfileImage.profileImage;

@Repository
@RequiredArgsConstructor
public class MatchQueryRepository {
    private static final int PAGE_SIZE = 13;
    private final JPAQueryFactory queryFactory;

    /**
     * 자신이 요청한 매칭 내역.
     */
    public List<MatchView> findSentMatches(long requesterId, Long lastMatchId) {
        Set<Long> blockedIds = getBlockedIds(requesterId);

        return queryFactory
            .select(new QMatchView(
                match.id,
                match.responderId,
                match.responseMessage != null ? match.responseMessage.value : null,
                member.profile.nickname.value,
                profileImage.imageUrl.value,
                member.profile.region.city.stringValue(),
                match.requestMessage != null ? match.requestMessage.value : null,
                match.status.stringValue(),
                match.createdAt
            ))
            .from(match)
            .where(
                eqRequesterId(requesterId),
                ltMatchId(lastMatchId),
                responderIdNotIn(blockedIds)
            )
            .join(member).on(member.id.eq(match.responderId))
            .leftJoin(profileImage).on(profileImage.memberId.eq(member.id).and(profileImage.isPrimary.eq(true)))
            .orderBy(match.id.desc())
            .limit(PAGE_SIZE)
            .fetch();
    }

    public List<MatchView> findReceiveMatches(long responderId, Long lastMatchId) {
        Set<Long> blockedIds = getBlockedIds(responderId);

        return queryFactory
            .select(new QMatchView(
                match.id,
                match.requesterId,
                match.responseMessage != null ? match.responseMessage.value : null,
                member.profile.nickname.value,
                profileImage.imageUrl.value,
                member.profile.region.city.stringValue(),
                match.requestMessage != null ? match.requestMessage.value : null,
                match.status.stringValue(),
                match.createdAt
            ))
            .from(match)
            .where(
                eqResponderId(responderId),
                ltMatchId(lastMatchId),
                requesterIdNotIn(blockedIds)
            )
            .join(member).on(member.id.eq(match.requesterId))
            .leftJoin(profileImage).on(profileImage.memberId.eq(member.id).and(profileImage.isPrimary.eq(true)))
            .orderBy(match.id.desc())
            .limit(PAGE_SIZE)
            .fetch();
    }


    private Set<Long> getBlockedIds(long blockerId) {
        return queryFactory
            .select(block.blockedId)
            .from(block)
            .where(block.blockerId.eq(blockerId))
            .fetch()
            .stream()
            .collect(Collectors.toSet());
    }

    private BooleanExpression eqRequesterId(long requesterId) {
        return match.requesterId.eq(requesterId);
    }

    private BooleanExpression eqResponderId(long responderId) {
        return match.responderId.eq(responderId);
    }

    private BooleanExpression requesterIdNotIn(Set<Long> ids) {
        if (ids.isEmpty()) {
            return null;
        }
        return match.requesterId.notIn(ids);
    }

    private BooleanExpression responderIdNotIn(Set<Long> ids) {
        if (ids.isEmpty()) {
            return null;
        }
        return match.responderId.notIn(ids);
    }

    private BooleanExpression ltMatchId(Long lastMatchId) {
        return lastMatchId != null ? match.id.lt(lastMatchId) : null;
    }
}
