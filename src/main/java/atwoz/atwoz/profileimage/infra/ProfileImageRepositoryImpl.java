package atwoz.atwoz.profileimage.infra;

import atwoz.atwoz.profileimage.domain.ProfileImage;
import atwoz.atwoz.profileimage.domain.ProfileImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfileImageRepositoryImpl implements ProfileImageRepository {

    private final ProfileImageJpaRepository profileImageJpaRepository;

    @Override
    public ProfileImage save(ProfileImage profileImage) {
        return profileImageJpaRepository.save(profileImage);
    }

    @Override
    public boolean existsByMemberIdAndIsPrimary(Long memberId) {
        return profileImageJpaRepository.existsByMemberIdAndIsPrimary(memberId, true);
    }
}
