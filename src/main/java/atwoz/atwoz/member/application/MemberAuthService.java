package atwoz.atwoz.member.application;

import atwoz.atwoz.auth.domain.TokenProvider;
import atwoz.atwoz.auth.domain.TokenRepository;
import atwoz.atwoz.common.enums.Role;
import atwoz.atwoz.member.application.dto.MemberLoginServiceDto;
import atwoz.atwoz.member.domain.member.Member;
import atwoz.atwoz.member.domain.member.MemberRepository;
import atwoz.atwoz.member.exception.MemberPermanentStopException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class MemberAuthService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final TokenRepository tokenRepository;

    @Transactional
    public MemberLoginServiceDto login(String phoneNumber) {
        Member member = createOrFindMemberByPhoneNumber(phoneNumber);

        if (member.isBanned()) {
            throw new MemberPermanentStopException();
        }

        String accessToken = tokenProvider.createAccessToken(member.getId(), Role.MEMBER, Instant.now());
        String refreshToken = tokenProvider.createRefreshToken(member.getId(), Role.MEMBER, Instant.now());
        tokenRepository.save(refreshToken);

        return MemberLoginServiceDto.fromMemberWithToken(accessToken, refreshToken, member.isProfileSettingNeeded());
    }

    public void logout(String token) {
        tokenRepository.delete(token);
    }

    private Member createOrFindMemberByPhoneNumber(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber).orElseGet(() -> create(phoneNumber));
    }

    private Member create(String phoneNumber) {
        return memberRepository.save(Member.fromPhoneNumber(phoneNumber));
    }
}
