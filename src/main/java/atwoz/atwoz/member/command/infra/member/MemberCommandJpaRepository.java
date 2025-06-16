package atwoz.atwoz.member.command.infra.member;

import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.vo.KakaoId;
import atwoz.atwoz.member.command.domain.member.vo.PhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

public interface MemberCommandJpaRepository extends JpaRepository<Member, Long> {
    Member save(Member member);

    Optional<Member> findByPhoneNumber(PhoneNumber phoneNumber);

    boolean existsByPhoneNumberAndIdNot(PhoneNumber phoneNumber, Long id);

    boolean existsByKakaoIdAndIdNot(KakaoId kakaoId, Long id);

    @Query("DELETE FROM Member m WHERE m.deletedAt <= :deletedAt")
    @Modifying(clearAutomatically = true)
    void deleteAllBefore(LocalDateTime deletedAt);

    Set<Long> findAllIdByIsProfilePublicFalse();
}
