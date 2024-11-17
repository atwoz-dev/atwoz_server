package awtoz.awtoz.member.infra;

import awtoz.awtoz.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
    Member save(Member member);
}
