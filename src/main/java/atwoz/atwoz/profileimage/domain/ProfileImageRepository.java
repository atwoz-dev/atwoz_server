package atwoz.atwoz.profileimage.domain;

import java.util.List;
import java.util.Optional;

public interface ProfileImageRepository {
    ProfileImage save(ProfileImage profileImage);

    boolean existsByMemberIdAndIsPrimary(Long memberId);
    void saveAll(List<ProfileImage> profileImages);
    Optional<ProfileImage> findById(Long id);
    void delete(ProfileImage profileImage);
}
