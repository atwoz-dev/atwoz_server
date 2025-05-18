package atwoz.atwoz.member.command.infra.member;

import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import atwoz.atwoz.member.command.domain.member.vo.KakaoId;
import atwoz.atwoz.member.command.domain.member.vo.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberCommandRepositoryImpl implements MemberCommandRepository {

    private final MemberCommandJpaRepository memberCommandJpaRepository;

    @Override
    public Member save(Member member) {
        return memberCommandJpaRepository.save(member);
    }

    @Override
    public Optional<Member> findById(Long id) {
        return memberCommandJpaRepository.findById(id);
    }

    @Override
    public Optional<Member> findByPhoneNumber(String phoneNumber) {
        return memberCommandJpaRepository.findByPhoneNumber(PhoneNumber.from(phoneNumber));
    }

    @Override
    public boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id) {
        return memberCommandJpaRepository.existsByPhoneNumberAndIdNot(PhoneNumber.from(phoneNumber), id);
    }

    @Override
    public boolean existsByKakaoIdAndIdNot(String kakaoId, Long id) {
        return memberCommandJpaRepository.existsByKakaoIdAndIdNot(KakaoId.from(kakaoId), id);
    }

    @Override
    public boolean existsById(Long id) {
        return memberCommandJpaRepository.existsById(id);
    }
}
