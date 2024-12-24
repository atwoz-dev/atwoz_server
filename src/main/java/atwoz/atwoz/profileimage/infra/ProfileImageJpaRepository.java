package atwoz.atwoz.profileimage.infra;

import atwoz.atwoz.profileimage.domain.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProfileImageJpaRepository extends JpaRepository<ProfileImage, Long> {
    boolean existsByMemberIdAndIsPrimary(Long memberId, boolean isPrimary);

    void delete(ProfileImage profileImage);
}
