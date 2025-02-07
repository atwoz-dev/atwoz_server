package atwoz.atwoz.member.command.application.member;

import atwoz.atwoz.auth.domain.TokenProvider;
import atwoz.atwoz.auth.domain.TokenRepository;
import atwoz.atwoz.common.enums.Role;
import atwoz.atwoz.member.command.application.member.dto.MemberLoginServiceDto;
import atwoz.atwoz.member.command.application.member.exception.BannedMemberException;
import atwoz.atwoz.member.command.application.member.exception.MemberLoginConflictException;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class MemberAuthService {

    private final MemberCommandRepository memberCommandRepository;
    private final TokenProvider tokenProvider;
    private final TokenRepository tokenRepository;

    @Transactional
    public MemberLoginServiceDto login(String phoneNumber) {
        Member member = createOrFindMemberByPhoneNumber(phoneNumber);

        if (member.isBanned()) {
            throw new BannedMemberException();
        }

        String accessToken = tokenProvider.createAccessToken(member.getId(), Role.MEMBER, Instant.now());
        String refreshToken = tokenProvider.createRefreshToken(member.getId(), Role.MEMBER, Instant.now());
        tokenRepository.save(refreshToken);

        return new MemberLoginServiceDto(accessToken, refreshToken, member.isProfileSettingNeeded());
    }

    public void logout(String token) {
        tokenRepository.delete(token);
    }

    private Member createOrFindMemberByPhoneNumber(String phoneNumber) {
        return memberCommandRepository.findByPhoneNumber(phoneNumber).orElseGet(() -> create(phoneNumber));
    }

    private Member create(String phoneNumber) {
        try {
            return memberCommandRepository.save(Member.fromPhoneNumber(phoneNumber));
        } catch (ConstraintViolationException e) {
            throw new MemberLoginConflictException(phoneNumber);
        }
    }
}
