package atwoz.atwoz.member.command.infra.profileImage;

import atwoz.atwoz.member.command.domain.profileImage.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ProfileImageJpaRepository extends JpaRepository<ProfileImage, Long> {
    boolean existsByMemberIdAndIsPrimary(Long memberId, boolean isPrimary);

    void delete(ProfileImage profileImage);

    List<ProfileImage> findByMemberId(Long memberId);

    @Modifying
    @Query("DELETE FROM ProfileImage pi WHERE pi.id IN :ids")
    void deleteInIds(List<Long> ids);
}
