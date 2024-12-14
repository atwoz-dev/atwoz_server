package atwoz.atwoz.profileimage.infra;

import atwoz.atwoz.profileimage.domain.ProfileImage;
import atwoz.atwoz.profileimage.domain.vo.MemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ProfileImageJpaRepository extends JpaRepository<ProfileImage, Long> {
    boolean existsByMemberIdAndIsPrimary(MemberId memberId, Boolean isPrimary);
    Optional<ProfileImage> findByIdAndMemberId(Long id, MemberId memberId);
    void delete(ProfileImage profileImage);
}
