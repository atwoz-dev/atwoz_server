package awtoz.awtoz.member.infra;

import awtoz.awtoz.member.domain.Member;
import awtoz.awtoz.member.domain.MemberRepository;
import awtoz.awtoz.member.domain.ProfileImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository jpaRepository;
    private final ProfileImageJpaRepository profileImageJpaRepository;

    @Override
    public Member save(Member member) {
        return jpaRepository.save(member);
    }

    @Override
    public ProfileImage saveRepProfileImage(ProfileImage profileImage) {
        return profileImageJpaRepository.save(profileImage);
    }
}
