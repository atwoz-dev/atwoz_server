package atwoz.atwoz.member.command.infra.member;

import atwoz.atwoz.member.command.domain.member.vo.KakaoId;
import atwoz.atwoz.member.command.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberCommandJpaRepository extends JpaRepository<Member, Long> {
    Member save(Member member);

    Optional<Member> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id);

    boolean existsByKakaoIdAndIdNot(KakaoId kakaoId, Long id);
}
