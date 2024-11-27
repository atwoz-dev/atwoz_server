package awtoz.awtoz.member.infra;

import awtoz.awtoz.member.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
    Member save(Member member);
    Optional<Member> findByPhoneNumber(String phoneNumber);
}
