package atwoz.atwoz.member.query.member;

import atwoz.atwoz.member.query.member.dto.MemberContactResponse;
import atwoz.atwoz.member.query.member.dto.MemberProfileResponse;
import atwoz.atwoz.member.query.member.dto.QMemberContactResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static atwoz.atwoz.hobby.domain.QHobby.hobby;
import static atwoz.atwoz.job.domain.QJob.job;
import static atwoz.atwoz.member.command.domain.member.QMember.member;
import static com.querydsl.core.types.Projections.constructor;
import static com.querydsl.core.types.dsl.Expressions.constant;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Optional<MemberProfileResponse> findProfileByMemberId(Long memberId) {
        List<String> hobbyNames = findHobbyNames(memberId);

        MemberProfileResponse memberProfileResponse = queryFactory
                .from(member)
                .leftJoin(job).on(job.id.eq(member.profile.jobId))
                .where(member.id.eq(memberId))
                .select(constructor(
                        MemberProfileResponse.class,
                        member.profile.nickname.value,
                        member.profile.age,
                        member.profile.gender.stringValue(),
                        member.profile.height,
                        job.name,
                        constant(hobbyNames),
                        member.profile.mbti.stringValue(),
                        member.profile.region.stringValue(),
                        member.profile.smokingStatus.stringValue(),
                        member.profile.drinkingStatus.stringValue(),
                        member.profile.highestEducation.stringValue(),
                        member.profile.religion.stringValue()
                ))
                .fetchOne();


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

    private List<String> findHobbyNames(Long memberId) {
        return queryFactory
                .select(hobby.name)
                .from(member)
                .join(hobby).on(hobby.id.in(member.profile.hobbyIds))
                .where(member.id.eq(memberId))
                .fetch();
    }
}
