package atwoz.atwoz.member.domain.member;

import java.util.Optional;


public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findByPhoneNumber(String phoneNumber);
}
