package atwoz.atwoz.profileimage.domain;

import java.util.List;
import java.util.Optional;

public interface ProfileImageRepository {
    ProfileImage save(ProfileImage profileImage);
    boolean existsPrimaryImageByMemberId(Long memberId);
    void saveAll(List<ProfileImage> profileImages);
    Optional<ProfileImage> findById(Long id);
    Optional<ProfileImage> findByIdAndMemberId(Long id, Long memberId);
    void delete(ProfileImage profileImage);
}
