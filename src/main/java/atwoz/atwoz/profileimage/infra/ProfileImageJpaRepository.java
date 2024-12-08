package atwoz.atwoz.profileimage.infra;

import atwoz.atwoz.profileimage.domain.ProfileImage;
import atwoz.atwoz.profileimage.domain.vo.MemberId;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProfileImageJpaRepository extends JpaRepository<ProfileImage, Long> {
    boolean existsByMemberIdAndIsPrimary(MemberId memberId, Boolean isPrimary);
}
