package atwoz.atwoz.member.query.profileimage;

import atwoz.atwoz.member.query.profileimage.view.ProfileImageView;
import atwoz.atwoz.member.query.profileimage.view.QProfileImageView;
import com.querydsl.jpa.JPQLQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static atwoz.atwoz.member.command.domain.profileImage.QProfileImage.profileImage;

@Repository
@RequiredArgsConstructor
public class ProfileImageQueryRepository {
    private final JPQLQueryFactory queryFactory;

    public List<ProfileImageView> findByMemberId(Long memberId) {
        return queryFactory
            .select(new QProfileImageView(
                profileImage.id,
                profileImage.imageUrl.value,
                profileImage.isPrimary,
                profileImage.order
            ))
            .from(profileImage)
            .where(profileImage.memberId.eq(memberId))
            .orderBy(profileImage.order.asc())
            .fetch();
    }
}
