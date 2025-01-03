package atwoz.atwoz.member.infra;

import atwoz.atwoz.member.domain.member.Member;
import atwoz.atwoz.member.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Member save(Member member) {
        return memberJpaRepository.save(member);
    }

    @Override
    public Optional<Member> findByPhoneNumber(String phoneNumber) {
        return memberJpaRepository.findByPhoneNumber(phoneNumber);
    }

    public Optional<Member> findById(Long id) {
        return memberJpaRepository.findById(id);
    }
}
