package awtoz.awtoz.member.application;

import awtoz.awtoz.common.auth.domain.Role;
import awtoz.awtoz.common.auth.infra.JwtProvider;
import awtoz.awtoz.member.application.dto.MemberLoginResponse;
import awtoz.awtoz.member.domain.member.Member;
import awtoz.awtoz.member.domain.member.MemberRepository;
import awtoz.awtoz.member.exception.MemberPermanentStopException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberAuthService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public MemberLoginResponse login(String phoneNumber) {
        Member member = createOrFindMemberByPhoneNumber(phoneNumber);

        if (member.isPermanentStop()) {
            throw new MemberPermanentStopException();
        }

        String accessToken = jwtProvider.createAccessToken(member.getId(), Role.MEMBER);
        return MemberLoginResponse.fromMemberWithToken(member, accessToken, "");
    }

    private Member createOrFindMemberByPhoneNumber(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber).orElse(create(phoneNumber));
    }

    private Member create(String phoneNumber) {
        Member member = memberRepository.save(Member.createFromPhoneNumber(phoneNumber));
        return member;
    }
}
