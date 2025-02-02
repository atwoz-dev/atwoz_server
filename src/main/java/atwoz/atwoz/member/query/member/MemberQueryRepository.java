package atwoz.atwoz.member.query.member;

import atwoz.atwoz.member.query.member.dto.MemberContactResponse;
import atwoz.atwoz.member.query.member.dto.MemberProfileResponse;
import atwoz.atwoz.member.query.member.dto.QMemberContactResponse;
import atwoz.atwoz.member.query.member.dto.QMemberProfileResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static atwoz.atwoz.hobby.command.domain.QHobby.hobby;
import static atwoz.atwoz.job.command.domain.QJob.job;
import static atwoz.atwoz.member.command.domain.member.QMember.member;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Optional<MemberProfileResponse> findProfileByMemberId(Long memberId) {
        MemberProfileResponse memberProfileResponse = queryFactory
                .from(member)
                .leftJoin(hobby).on(hobby.id.in(member.profile.hobbyIds))
                .leftJoin(job).on(job.id.eq(member.profile.jobId))
                .where(member.id.eq(memberId))
                .transform(
                        groupBy(member.id).as(
                                new QMemberProfileResponse(
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
                ).get(memberId); // 특정 memberId의 결과 가져오기


        return Optional.ofNullable(memberProfileResponse);
    }

    public Optional<MemberContactResponse> findContactsByMemberId(Long memberId) {
        MemberContactResponse memberContactResponse = queryFactory
                .from(member)
                .where(member.id.eq(memberId))
                .select(new QMemberContactResponse(
                        member.phoneNumber,
                        member.kakaoId.value,
                        member.primaryContactType.stringValue())
                ).fetchOne();

        return Optional.ofNullable(memberContactResponse);
    }
}
