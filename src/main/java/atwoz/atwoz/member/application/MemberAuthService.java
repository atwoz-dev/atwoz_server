package atwoz.atwoz.member.application;

import atwoz.atwoz.auth.context.Role;
import atwoz.atwoz.auth.jwt.JwtProvider;
import atwoz.atwoz.auth.jwt.JwtRepository;
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
    private final JwtProvider jwtProvider;
    private final JwtRepository jwtRepository;

    @Transactional
    public MemberLoginServiceDto login(String phoneNumber) {
        Member member = createOrFindMemberByPhoneNumber(phoneNumber);

        if (member.isPermanentStop()) {
            throw new MemberPermanentStopException();
        }

        String accessToken = jwtProvider.createAccessToken(member.getId(), Role.MEMBER, Instant.now());
        String refreshToken = jwtProvider.createRefreshToken(member.getId(), Role.MEMBER, Instant.now());
        jwtRepository.save(refreshToken);

        return MemberLoginServiceDto.fromMemberWithToken(accessToken, refreshToken, member.isProfileSettingNeeded());
    }

    public void logout(String token) {
        jwtRepository.delete(token);
    }

    private Member createOrFindMemberByPhoneNumber(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber).orElse(create(phoneNumber));
    }

    private Member create(String phoneNumber) {
        return memberRepository.save(Member.createFromPhoneNumber(phoneNumber));
    }
}
