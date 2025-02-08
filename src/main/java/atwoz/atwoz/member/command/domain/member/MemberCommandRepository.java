package atwoz.atwoz.member.command.domain.member;

import java.util.Optional;


public interface MemberCommandRepository {
    Member save(Member member);

    Optional<Member> findById(Long id);

    Optional<Member> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id);

    boolean existsByKakaoIdAndIdNot(String kakaoId, Long id);

    void flush();
}
