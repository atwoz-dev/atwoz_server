package atwoz.atwoz.member.command.infra.profileImage;

import atwoz.atwoz.member.command.domain.profileImage.ProfileImage;
import atwoz.atwoz.member.command.domain.profileImage.ProfileImageCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProfileImageCommandRepositoryImpl implements ProfileImageCommandRepository {

    private final ProfileImageJpaRepository profileImageJpaRepository;

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
        profileImageJpaRepository.saveAll(profileImages);
    }

    @Override
    public Optional<ProfileImage> findById(Long id) {
        return profileImageJpaRepository.findById(id);
    }

    @Override
    public void delete(ProfileImage profileImage) {
        profileImageJpaRepository.delete(profileImage);
    }

    @Override
    public java.util.List<ProfileImage> findByMemberId(Long memberId) {
        return profileImageJpaRepository.findByMemberId(memberId);
    }

    @Override
    public void deleteByMemberId(Long memberId) {
        profileImageJpaRepository.deleteAllByMemberId(memberId);
    }
}
