package awtoz.awtoz.member.infra;

import awtoz.awtoz.member.domain.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileImageJpaRepository extends JpaRepository<ProfileImage, Long> {
    ProfileImage save(ProfileImage profileImage);
}
