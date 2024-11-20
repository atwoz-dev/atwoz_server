package awtoz.awtoz.member.application.auth;

import awtoz.awtoz.member.application.auth.dto.MemberLoginResponse;
import awtoz.awtoz.member.infra.auth.MemberJwtTokenProvider;
import awtoz.awtoz.member.domain.member.Member;
import awtoz.awtoz.member.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberAuthService {

    private final MemberRepository memberRepository;
    private final MemberJwtTokenProvider memberJwtTokenProvider;


    public MemberLoginResponse login(String phoneNumber) {
        Member member = createOrFindMemberByPhoneNumber(phoneNumber);
        String accessToken = memberJwtTokenProvider.createAccessToken(member.getId());
        return MemberLoginResponse.fromMemberWithToken(member, accessToken, "");
    }

    private Member createOrFindMemberByPhoneNumber(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber).orElse(create(phoneNumber));
    }

    private Member create(String phoneNumber) {
        Member member = memberRepository.save(Member.createWithPhoneNumber(phoneNumber));
        return member;
    }
}
