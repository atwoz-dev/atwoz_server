package atwoz.atwoz.member.command.application.member;

import atwoz.atwoz.member.command.application.member.exception.MemberLoginConflictException;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberServiceSupport {
    private final MemberCommandRepository memberCommandRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected Member create(String phoneNumber) {
        try {
            return memberCommandRepository.save(Member.fromPhoneNumber(phoneNumber));
        } catch (DataIntegrityViolationException e) {
            throw new MemberLoginConflictException(phoneNumber);
        }
    }
}
