package atwoz.atwoz.member.query.introduction.intra;

import atwoz.atwoz.member.command.domain.member.City;
import atwoz.atwoz.member.command.domain.member.Hobby;
import atwoz.atwoz.member.query.introduction.application.MemberIdealView;
import atwoz.atwoz.member.query.introduction.application.QMemberIdealView;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static atwoz.atwoz.admin.command.domain.hobby.QHobby.hobby;
import static atwoz.atwoz.member.command.domain.introduction.QMemberIdeal.memberIdeal;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.set;
import static com.querydsl.core.types.dsl.Expressions.enumPath;

@Repository
@RequiredArgsConstructor
public class MemberIdealQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Optional<MemberIdealView> findMemberIdealByMemberId(long memberId) {
        EnumPath<City> city = enumPath(City.class, "cityAlias");
        EnumPath<Hobby> hobby = enumPath(Hobby.class, "hobbyAlias");

        MemberIdealView memberIdealView = queryFactory
                .from(memberIdeal)
                .where(memberIdeal.memberId.eq(memberId))
                .leftJoin(memberIdeal.hobbies, hobby)
                .leftJoin(memberIdeal.cities, city)
                .transform(
                        groupBy(memberIdeal.memberId).as(
                                new QMemberIdealView(
                                        memberIdeal.ageRange.minAge,
                                        memberIdeal.ageRange.maxAge,
                                        set(hobby.stringValue()),
                                        set(city.stringValue()),
                                        memberIdeal.religion.stringValue(),
                                        memberIdeal.smokingStatus.stringValue(),
                                        memberIdeal.drinkingStatus.stringValue()
                                )
                        )
                ).get(memberId);

        return Optional.ofNullable(memberIdealView);
    }
}
