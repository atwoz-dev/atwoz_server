package atwoz.atwoz.member.query.member;

import atwoz.atwoz.match.command.domain.match.MatchStatus;
import atwoz.atwoz.member.command.domain.member.PrimaryContactType;
import atwoz.atwoz.member.query.member.view.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static atwoz.atwoz.admin.command.domain.hobby.QHobby.hobby;
import static atwoz.atwoz.admin.command.domain.job.QJob.job;
import static atwoz.atwoz.interview.command.domain.answer.QInterviewAnswer.interviewAnswer;
import static atwoz.atwoz.interview.command.domain.question.QInterviewQuestion.interviewQuestion;
import static atwoz.atwoz.match.command.domain.match.QMatch.match;
import static atwoz.atwoz.member.command.domain.member.QMember.member;
import static atwoz.atwoz.member.command.domain.profileImage.QProfileImage.profileImage;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.types.dsl.Expressions.cases;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Optional<MemberProfileView> findProfileByMemberId(Long memberId) {
        MemberProfileView memberProfileView = queryFactory
                .from(member)
                .leftJoin(hobby).on(hobby.id.in(member.profile.hobbyIds))
                .leftJoin(job).on(job.id.eq(member.profile.jobId))
                .where(member.id.eq(memberId))
                .transform(
                        groupBy(member.id).as(
                                new QMemberProfileView(
                                        member.profile.nickname.value,
                                        member.profile.age,
                                        member.profile.gender.stringValue(),
                                        member.profile.height,
                                        job.name,
                                        list(hobby.name),
                                        member.profile.mbti.stringValue(),
                                        member.profile.region.stringValue(),
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
        OtherMemberProfileView otherMemberProfileView = queryFactory
                .from(member)
                .leftJoin(hobby).on(hobby.id.in(member.profile.hobbyIds))
                .leftJoin(job).on(job.id.eq(member.profile.jobId))
                .leftJoin(profileImage).on(profileImage.memberId.eq(otherMemberId).and(profileImage.isPrimary.eq(true)))
                .leftJoin(match).on(getMatchJoinCondition(memberId, otherMemberId))
                .where(member.id.eq(otherMemberId))
                .transform(
                        groupBy(member.id).as(
                                new QOtherMemberProfileView(
                                        member.id,
                                        member.profile.nickname.value,
                                        profileImage.imageUrl.value,
                                        member.profile.age,
                                        member.profile.gender.stringValue(),
                                        member.profile.height,
                                        job.name,
                                        list(hobby.name),
                                        member.profile.mbti.stringValue(),
                                        member.profile.region.stringValue(),
                                        member.profile.smokingStatus.stringValue(),
                                        member.profile.drinkingStatus.stringValue(),
                                        member.profile.highestEducation.stringValue(),
                                        member.profile.religion.stringValue(),
                                        match.id,
                                        match.requesterId,
                                        match.responderId,
                                        match.requestMessage.value,
                                        match.responseMessage.value,
                                        match.status.stringValue(),
                                        member.primaryContactType.stringValue(),
                                        cases()
                                                .when(match.status.eq(MatchStatus.MATCHED).and(member.primaryContactType.eq(PrimaryContactType.PHONE_NUMBER)))
                                                .then(member.phoneNumber.value)
                                                .when(match.status.eq(MatchStatus.MATCHED).and(member.primaryContactType.eq(PrimaryContactType.KAKAO)))
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
                        new QInterviewResultView(interviewQuestion.content, interviewQuestion.category.stringValue(), interviewAnswer.content)
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
}
