package atwoz.atwoz.member.query.member;

import atwoz.atwoz.member.query.member.view.MemberContactView;
import atwoz.atwoz.member.query.member.view.MemberProfileView;
import atwoz.atwoz.member.query.member.view.QMemberContactView;
import atwoz.atwoz.member.query.member.view.QMemberProfileView;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static atwoz.atwoz.admin.command.domain.hobby.QHobby.hobby;
import static atwoz.atwoz.admin.command.domain.job.QJob.job;
import static atwoz.atwoz.member.command.domain.member.QMember.member;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

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
                .from(member)
                .where(member.id.eq(memberId))
                .select(new QMemberContactView(
                        member.phoneNumber.value,
                        member.kakaoId.value,
                        member.primaryContactType.stringValue())
                ).fetchOne();

        return Optional.ofNullable(memberContactView);
    }
}
