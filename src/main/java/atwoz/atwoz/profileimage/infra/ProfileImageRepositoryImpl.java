package atwoz.atwoz.profileimage.infra;

import atwoz.atwoz.profileimage.domain.ProfileImage;
import atwoz.atwoz.profileimage.domain.ProfileImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProfileImageRepositoryImpl implements ProfileImageRepository {

    private final ProfileImageJpaRepository profileImageJpaRepository;
    private final ProfileImageJdbcRepository profileImageJdbcRepository;

    @Override
    public ProfileImage save(ProfileImage profileImage) {
        return profileImageJpaRepository.save(profileImage);
    }

    @Override
    public boolean existsByMemberIdAndIsPrimary(Long memberId) {
        return profileImageJpaRepository.existsByMemberIdAndIsPrimary(memberId, true);
    }

    @Override
    public void saveAll(List<ProfileImage> profileImages) {
        profileImageJdbcRepository.saveAll(profileImages);
    }

    @Override
    public Optional<ProfileImage> findById(Long id) {
        return profileImageJpaRepository.findById(id);
    }

    @Override
    public void delete(ProfileImage profileImage) {
        profileImageJpaRepository.delete(profileImage);
    }
}
