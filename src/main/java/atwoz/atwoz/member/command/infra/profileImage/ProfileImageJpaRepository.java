package atwoz.atwoz.member.command.infra.profileImage;

import atwoz.atwoz.member.command.domain.profileImage.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProfileImageJpaRepository extends JpaRepository<ProfileImage, Long> {
    boolean existsByMemberIdAndIsPrimary(Long memberId, boolean isPrimary);

    void delete(ProfileImage profileImage);
}
