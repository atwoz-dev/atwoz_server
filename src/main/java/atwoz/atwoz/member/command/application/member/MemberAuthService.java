package atwoz.atwoz.member.command.application.member;

import atwoz.atwoz.auth.domain.TokenParser;
import atwoz.atwoz.auth.domain.TokenProvider;
import atwoz.atwoz.auth.domain.TokenRepository;
import atwoz.atwoz.common.enums.Role;
import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.member.command.application.member.dto.MemberLoginServiceDto;
import atwoz.atwoz.member.command.application.member.exception.*;
import atwoz.atwoz.member.command.application.member.sms.AuthMessageService;
import atwoz.atwoz.member.command.domain.member.ActivityStatus;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import atwoz.atwoz.member.command.domain.member.event.MemberLoggedInEvent;
import atwoz.atwoz.member.command.domain.member.event.MemberLoggedOutEvent;
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
        if (PRE_FIXED_CODE == null || PRE_FIXED_CODE.isBlank() || !PRE_FIXED_CODE.equals(code)) {
            authMessageService.authenticate(phoneNumber, code);
        }

        Member member = createOrFindMemberByPhoneNumber(phoneNumber);
        validateMemberLoginPermission(member);

        String accessToken = tokenProvider.createAccessToken(member.getId(), Role.MEMBER, Instant.now());
        String refreshToken = tokenProvider.createRefreshToken(member.getId(), Role.MEMBER, Instant.now());
        tokenRepository.save(refreshToken);

        Events.raise(MemberLoggedInEvent.from(member.getId()));

        return new MemberLoginServiceDto(accessToken, refreshToken, member.isProfileSettingNeeded(),
            member.getActivityStatus() != null ? member.getActivityStatus().name() : null);
    }

    @Transactional
    public MemberLoginServiceDto test(String phoneNumber) {
        Member member = createOrFindMemberByPhoneNumber(phoneNumber);

        validateMemberLoginPermission(member);

        String accessToken = tokenProvider.createAccessToken(member.getId(), Role.MEMBER, Instant.now());
        String refreshToken = tokenProvider.createRefreshToken(member.getId(), Role.MEMBER, Instant.now());
        tokenRepository.save(refreshToken);

        return new MemberLoginServiceDto(accessToken, refreshToken, member.isProfileSettingNeeded(),
            member.getActivityStatus() != null ? member.getActivityStatus().name() : null);
    }

    public void logout(long memberId, String token) {
        Events.raise(MemberLoggedOutEvent.from(memberId));
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

    private void validateMemberLoginPermission(Member member) {
        if (member.isDeleted()) { // 회원이 삭제된 경우.
            throw new MemberDeletedException();
        }

        ActivityStatus activityStatus = member.getActivityStatus();

        if (activityStatus == ActivityStatus.SUSPENDED_PERMANENTLY) { // 영구 정지일 경우.
            throw new PermanentlySuspendedMemberException();
        } else if (activityStatus == ActivityStatus.SUSPENDED_TEMPORARILY) { // 일시 정지일 경우.
            throw new TemporarilySuspendedMemberException();
        } else if (activityStatus != ActivityStatus.ACTIVE && activityStatus != ActivityStatus.INITIAL) { // 활동중이 아닐 경우.
            throw new MemberNotActiveException();
        }
    }
}
