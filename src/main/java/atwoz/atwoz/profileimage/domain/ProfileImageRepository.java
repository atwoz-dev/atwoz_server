package atwoz.atwoz.profileimage.domain;

import java.util.List;

public interface ProfileImageRepository {
    ProfileImage save(ProfileImage profileImage);
    boolean existsByMemberIdAndIsPrimary(Long memberId);
    void saveAll(List<ProfileImage> profileImages);
}
