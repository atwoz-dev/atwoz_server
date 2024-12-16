package atwoz.atwoz.member.application;

import atwoz.atwoz.common.auth.context.Role;
import atwoz.atwoz.common.auth.jwt.JwtProvider;
import atwoz.atwoz.member.application.dto.MemberLoginResponse;
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

    @Transactional
    public MemberLoginResponse login(String phoneNumber) {
        Member member = createOrFindMemberByPhoneNumber(phoneNumber);

        if (member.isPermanentStop()) {
            throw new MemberPermanentStopException();
        }

        String accessToken = jwtProvider.createAccessToken(member.getId(), Role.MEMBER, Instant.now());
        return MemberLoginResponse.fromMemberWithToken(member, accessToken, "");
    }

    private Member createOrFindMemberByPhoneNumber(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber).orElse(create(phoneNumber));
    }

    private Member create(String phoneNumber) {
        return memberRepository.save(Member.createFromPhoneNumber(phoneNumber));
    }
}
