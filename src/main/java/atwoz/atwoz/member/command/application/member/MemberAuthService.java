package atwoz.atwoz.member.command.application.member;

import atwoz.atwoz.auth.domain.TokenProvider;
import atwoz.atwoz.auth.domain.TokenRepository;
import atwoz.atwoz.common.enums.Role;
import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.member.command.application.member.dto.MemberLoginServiceDto;
import atwoz.atwoz.member.command.application.member.exception.MemberLoginConflictException;
import atwoz.atwoz.member.command.application.member.exception.PermanentlySuspendedMemberException;
import atwoz.atwoz.member.command.application.member.sms.AuthMessageService;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import atwoz.atwoz.member.command.domain.member.event.MemberRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class MemberAuthService {

    private final MemberCommandRepository memberCommandRepository;
    private final AuthMessageService authMessageService;
    private final TokenProvider tokenProvider;
    private final TokenRepository tokenRepository;

    @Transactional
    public MemberLoginServiceDto login(String phoneNumber, String code) {
        authMessageService.authenticate(phoneNumber, code);
        Member member = createOrFindMemberByPhoneNumber(phoneNumber);
        if (member.isPermanentlySuspended()) {
            throw new PermanentlySuspendedMemberException();
        }

        String accessToken = tokenProvider.createAccessToken(member.getId(), Role.MEMBER, Instant.now());
        String refreshToken = tokenProvider.createRefreshToken(member.getId(), Role.MEMBER, Instant.now());
        tokenRepository.save(refreshToken);

        return new MemberLoginServiceDto(accessToken, refreshToken, member.isProfileSettingNeeded());
    }

    public void logout(String token) {
        tokenRepository.delete(token);
    }

    public void sendAuthCode(String phoneNumber) {
        authMessageService.sendAndSaveCode(phoneNumber);
    }

    private Member createOrFindMemberByPhoneNumber(String phoneNumber) {
        return memberCommandRepository.findByPhoneNumber(phoneNumber).orElseGet(() -> create(phoneNumber));
    }

    private Member create(String phoneNumber) {
        try {
            Member member = memberCommandRepository.save(Member.fromPhoneNumber(phoneNumber));
            Events.raise(new MemberRegisteredEvent(member.getId())); // Event 발행.
            return member;
        } catch (DataIntegrityViolationException e) {
            throw new MemberLoginConflictException(phoneNumber);
        }
    }
}
