package atwoz.atwoz.member.query.introduction.intra;

import atwoz.atwoz.member.query.introduction.application.MemberIdealView;
import atwoz.atwoz.member.query.introduction.application.QMemberIdealView;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static atwoz.atwoz.admin.command.domain.hobby.QHobby.hobby;
import static atwoz.atwoz.member.command.domain.introduction.QMemberIdeal.memberIdeal;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

@Repository
@RequiredArgsConstructor
public class MemberIdealQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Optional<MemberIdealView> findMemberIdealByMemberId(long memberId) {
        MemberIdealView memberIdealView = queryFactory
                .from(memberIdeal)
                .where(memberIdeal.memberId.eq(memberId))
                .leftJoin(hobby).on(hobby.id.in(memberIdeal.hobbyIds))
                .transform(
                        groupBy(memberIdeal.memberId).as(
                                new QMemberIdealView(
                                        memberIdeal.ageRange.minAge,
                                        memberIdeal.ageRange.maxAge,
                                        list(hobby.name),
                                        memberIdeal.region.stringValue(),
                                        memberIdeal.religion.stringValue(),
                                        memberIdeal.smokingStatus.stringValue(),
                                        memberIdeal.drinkingStatus.stringValue()
                                )
                        )
                ).get(memberId);

        return Optional.ofNullable(memberIdealView);
    }
}
