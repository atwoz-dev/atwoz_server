package atwoz.atwoz.member.query.member;

import atwoz.atwoz.match.command.domain.match.MatchStatus;
import atwoz.atwoz.member.command.domain.member.Hobby;
import atwoz.atwoz.member.command.domain.member.PrimaryContactType;
import atwoz.atwoz.member.query.member.view.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static atwoz.atwoz.interview.command.domain.answer.QInterviewAnswer.interviewAnswer;
import static atwoz.atwoz.interview.command.domain.question.QInterviewQuestion.interviewQuestion;
import static atwoz.atwoz.like.command.domain.QLike.like;
import static atwoz.atwoz.match.command.domain.match.QMatch.match;
import static atwoz.atwoz.member.command.domain.member.QMember.member;
import static atwoz.atwoz.member.command.domain.profileImage.QProfileImage.profileImage;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.set;
import static com.querydsl.core.types.dsl.Expressions.cases;
import static com.querydsl.core.types.dsl.Expressions.enumPath;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Optional<MemberProfileView> findProfileByMemberId(Long memberId) {
        EnumPath<Hobby> hobby = enumPath(Hobby.class, "hobbyAlias");

        MemberProfileView memberProfileView = queryFactory
            .from(member)
            .leftJoin(member.profile.hobbies, hobby)
            .where(member.id.eq(memberId))
            .transform(
                groupBy(member.id).as(
                    new QMemberProfileView(
                        member.profile.nickname.value,
                        member.profile.yearOfBirth.value,
                        member.profile.gender.stringValue(),
                        member.profile.height,
                        member.profile.job.stringValue(),
                        set(hobby.stringValue()),
                        member.profile.mbti.stringValue(),
                        member.profile.region.city.stringValue(),
                        member.profile.region.district.stringValue(),
                        member.profile.smokingStatus.stringValue(),
                        member.profile.drinkingStatus.stringValue(),
                        member.profile.highestEducation.stringValue(),
                        member.profile.religion.stringValue()
                    )
                )
            ).get(memberId);


        return Optional.ofNullable(memberProfileView);
    }

    public Optional<MemberContactView> findContactsByMemberId(Long memberId) {
        MemberContactView memberContactView = queryFactory
            .select(new QMemberContactView(
                member.phoneNumber.value,
                member.kakaoId.value,
                member.primaryContactType.stringValue())
            )
            .from(member)
            .where(member.id.eq(memberId))
            .fetchOne();

        return Optional.ofNullable(memberContactView);
    }

    public Optional<OtherMemberProfileView> findOtherProfileByMemberId(Long memberId, Long otherMemberId) {
        EnumPath<Hobby> hobby = enumPath(Hobby.class, "hobbyAlias");

        OtherMemberProfileView otherMemberProfileView = queryFactory
            .from(member)
            .leftJoin(member.profile.hobbies, hobby)
            .leftJoin(profileImage).on(profileImage.memberId.eq(otherMemberId).and(profileImage.isPrimary.eq(true)))
            .leftJoin(match).on(getMatchJoinCondition(memberId, otherMemberId))
            .leftJoin(like).on(like.senderId.eq(memberId).and(like.receiverId.eq(otherMemberId)))
            .where(member.id.eq(otherMemberId))
            .transform(
                groupBy(member.id).as(
                    new QOtherMemberProfileView(
                        member.id,
                        member.profile.nickname.value,
                        profileImage.imageUrl.value,
                        member.profile.yearOfBirth.value,
                        member.profile.gender.stringValue(),
                        member.profile.height,
                        member.profile.job.stringValue(),
                        set(hobby.stringValue()),
                        member.profile.mbti.stringValue(),
                        member.profile.region.city.stringValue(),
                        member.profile.smokingStatus.stringValue(),
                        member.profile.drinkingStatus.stringValue(),
                        member.profile.highestEducation.stringValue(),
                        member.profile.religion.stringValue(),
                        like.level.stringValue(),
                        match.id,
                        match.requesterId,
                        match.responderId,
                        match.requestMessage.value,
                        match.responseMessage.value,
                        match.status.stringValue(),
                        member.primaryContactType.stringValue(),
                        cases()
                            .when(match.status.eq(MatchStatus.MATCHED)
                                .and(member.primaryContactType.eq(PrimaryContactType.PHONE_NUMBER)))
                            .then(member.phoneNumber.value)
                            .when(match.status.eq(MatchStatus.MATCHED)
                                .and(member.primaryContactType.eq(PrimaryContactType.KAKAO)))
                            .then(member.kakaoId.value)
                            .otherwise((String) null)
                    )
                )).get(otherMemberId);


        return Optional.ofNullable(otherMemberProfileView);
    }

    public List<InterviewResultView> findInterviewsByMemberId(Long memberId) {
        return queryFactory
            .from(interviewQuestion)
            .innerJoin(interviewAnswer).on(getInterviewAnswerJoinCondition(memberId))
            .select(
                new QInterviewResultView(interviewQuestion.content, interviewQuestion.category.stringValue(),
                    interviewAnswer.content)
            )
            .where(interviewQuestion.isPublic.eq(true))
            .fetch();
    }

    private BooleanExpression getMatchJoinCondition(Long memberId, Long otherMemberId) {
        return (match.requesterId.eq(memberId).and(match.responderId.eq(otherMemberId))
            .or(match.requesterId.eq(otherMemberId).and(match.responderId.eq(memberId))))
            .and(match.status.notIn(MatchStatus.REJECT_CHECKED, MatchStatus.EXPIRED));
    }

    private BooleanExpression getInterviewAnswerJoinCondition(Long memberId) {
        return interviewQuestion.id.eq(interviewAnswer.id).and(interviewAnswer.memberId.eq(memberId));
    }

    public HeartBalanceView findHeartBalanceByMemberId(long memberId) {
        return queryFactory
            .select(new QHeartBalanceView(
                member.heartBalance.purchaseHeartBalance,
                member.heartBalance.missionHeartBalance,
                member.heartBalance.purchaseHeartBalance.add(member.heartBalance.missionHeartBalance)
            ))
            .from(member)
            .where(member.id.eq(memberId))
            .fetchOne();
    }
}
