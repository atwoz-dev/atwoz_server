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

    // TODO : 유저 엔터티를 기반으로 토큰 생성.
    public MemberLoginResponse login(String phoneNumber) {

        Member loginMember = null;

        try {
            loginMember = findMemberByPhoneNumber(phoneNumber);

        } catch (RuntimeException memberNotFound) { // 회원이 없는 경우 생성 후, 토큰 생성.
            loginMember = create(phoneNumber);

        } finally {
            String accessToken = memberJwtTokenProvider.createAccessToken(loginMember.getId());
            // TODO :  추가 정보가 필요하진 확인
            return MemberLoginResponse.fromMemberWithToken(loginMember, accessToken, "", true);
        }
    }

    private Member create(String phoneNumber) {
        Member member = memberRepository.save(Member.createWithPhoneNumber(phoneNumber));
        return member;
    }

    private Member findMemberByPhoneNumber(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new RuntimeException("MemberNotFound"));
    }

}
