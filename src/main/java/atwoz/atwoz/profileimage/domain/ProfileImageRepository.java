package atwoz.atwoz.profileimage.domain;

public interface ProfileImageRepository {
    ProfileImage save(ProfileImage profileImage);
    boolean existsByMemberIdAndIsPrimary(Long memberId);
}
