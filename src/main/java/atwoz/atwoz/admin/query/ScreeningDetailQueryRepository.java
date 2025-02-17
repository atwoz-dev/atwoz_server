package atwoz.atwoz.admin.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static atwoz.atwoz.admin.command.domain.screening.QScreening.screening;
import static atwoz.atwoz.member.command.domain.member.QMember.member;
import static atwoz.atwoz.member.command.domain.profileImage.QProfileImage.profileImage;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

@Repository
@RequiredArgsConstructor
public class ScreeningDetailQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ScreeningDetailView findById(Long screeningId) {
        return queryFactory
                .from(member)
                .join(screening).on(screening.memberId.eq(member.id))
                .leftJoin(profileImage).on(profileImage.memberId.eq(member.id))
                .where(screening.id.eq(screeningId))
                .transform(
                        groupBy(screening.id).as(
                                new QScreeningDetailView(
                                        screening.id,
                                        member.id,
                                        screening.status.stringValue(),
                                        screening.rejectionReason.stringValue(),
                                        member.profile.nickname.value,
                                        member.profile.age,
                                        member.profile.gender.stringValue(),
                                        member.createdAt.stringValue(),
                                        list(new QProfileImageView(
                                                profileImage.imageUrl.value,
                                                profileImage.order,
                                                profileImage.isPrimary
                                        ))
                                )
                        )
                )
                .get(screeningId);
    }
}
