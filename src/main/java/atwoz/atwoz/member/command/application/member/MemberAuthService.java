package atwoz.atwoz.member.command.application.member;

import atwoz.atwoz.auth.domain.TokenParser;
import atwoz.atwoz.auth.domain.TokenProvider;
import atwoz.atwoz.auth.domain.TokenRepository;
import atwoz.atwoz.common.enums.Role;
import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.member.command.application.member.dto.MemberLoginServiceDto;
import atwoz.atwoz.member.command.application.member.exception.MemberDeletedException;
import atwoz.atwoz.member.command.application.member.exception.MemberLoginConflictException;
import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.command.application.member.exception.PermanentlySuspendedMemberException;
import atwoz.atwoz.member.command.application.member.sms.AuthMessageService;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import atwoz.atwoz.member.command.domain.member.event.MemberRegisteredEvent;
import atwoz.atwoz.member.command.domain.member.exception.MemberNotActiveException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    private final TokenParser tokenParser;
    private final TokenRepository tokenRepository;
    @Value("${auth.prefix-code}")
    private String PRE_FIXED_CODE;

    @Transactional
    public MemberLoginServiceDto login(String phoneNumber, String code) {
        if (!PRE_FIXED_CODE.equals(code)) {
            authMessageService.authenticate(phoneNumber, code);
        }
        
        Member member = createOrFindMemberByPhoneNumber(phoneNumber);

        if (member.isDeleted()) {
            throw new MemberDeletedException();
        }

        if (member.isPermanentlySuspended()) {
            throw new PermanentlySuspendedMemberException();
        }

        if (!member.isActive()) {
            throw new MemberNotActiveException();
        }

        String accessToken = tokenProvider.createAccessToken(member.getId(), Role.MEMBER, Instant.now());
        String refreshToken = tokenProvider.createRefreshToken(member.getId(), Role.MEMBER, Instant.now());
        tokenRepository.save(refreshToken);

        return new MemberLoginServiceDto(accessToken, refreshToken, member.isProfileSettingNeeded());
    }

    @Transactional
    public MemberLoginServiceDto test(String phoneNumber) {
        Member member = createOrFindMemberByPhoneNumber(phoneNumber);

        if (member.isDeleted()) {
            throw new MemberDeletedException();
        }

        if (member.isPermanentlySuspended()) {
            throw new PermanentlySuspendedMemberException();
        }

        if (!member.isActive()) {
            throw new MemberNotActiveException();
        }

        String accessToken = tokenProvider.createAccessToken(member.getId(), Role.MEMBER, Instant.now());
        String refreshToken = tokenProvider.createRefreshToken(member.getId(), Role.MEMBER, Instant.now());
        tokenRepository.save(refreshToken);

        return new MemberLoginServiceDto(accessToken, refreshToken, member.isProfileSettingNeeded());
    }

    public void logout(String token) {
        deleteToken(token);
    }

    @Transactional
    public void delete(Long memberId, String token) {
        Member member = getMemberById(memberId);

        if (tokenParser.isValid(token) && tokenParser.getId(token) == member.getId()) {
            deleteToken(token);
        }
        member.delete();
    }

    public void sendAuthCode(String phoneNumber) {
        authMessageService.sendAndSaveCode(phoneNumber);
    }

    private void deleteToken(String token) {
        tokenRepository.delete(token);
    }

    private Member createOrFindMemberByPhoneNumber(String phoneNumber) {
        return memberCommandRepository.findByPhoneNumber(phoneNumber).orElseGet(() -> create(phoneNumber));
    }

    private Member getMemberById(Long memberId) {
        return memberCommandRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
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
