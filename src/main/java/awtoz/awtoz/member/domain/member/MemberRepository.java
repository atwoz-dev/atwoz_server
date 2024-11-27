package awtoz.awtoz.member.domain.member;

import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findByPhoneNumber(String phoneNumber);
}
