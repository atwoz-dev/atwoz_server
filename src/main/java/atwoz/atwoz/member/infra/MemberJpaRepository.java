package atwoz.atwoz.member.infra;

import atwoz.atwoz.member.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
    Member save(Member member);
    Optional<Member> findByPhoneNumber(String phoneNumber);
}
