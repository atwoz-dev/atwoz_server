package atwoz.atwoz.member.application;

import atwoz.atwoz.member.application.dto.MemberContactResponse;
import atwoz.atwoz.member.application.exception.KakaoIdAlreadyExistsException;
import atwoz.atwoz.member.application.exception.MemberNotFoundException;
import atwoz.atwoz.member.application.exception.PhoneNumberAlreadyExistsException;
import atwoz.atwoz.member.domain.member.KakaoId;
import atwoz.atwoz.member.domain.member.Member;
import atwoz.atwoz.member.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberContactService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MemberContactResponse getContacts(Long memberId) {
        return MemberMapper.toMemberContactResponse(getMemberById(memberId));
    }

    @Transactional
    public void updateKakaoId(Long memberId, String kakaoId) {
        validateKakaoId(kakaoId, memberId);
        Member member = getMemberById(memberId);
        member.changePrimaryContactTypeToKakao(KakaoId.from(kakaoId));
    }

    @Transactional
    public void updatePhoneNumber(Long memberId, String phoneNumber) {
        validatePhoneNumber(phoneNumber, memberId);
        Member member = getMemberById(memberId);
        member.changePrimaryContactTypeToPhoneNumber(phoneNumber);
    }

    private void validatePhoneNumber(String phoneNumber, Long memberId) {
        if (memberRepository.existsByPhoneNumberAndIdNot(phoneNumber, memberId)) {
            throw new PhoneNumberAlreadyExistsException();
        }
    }

    private void validateKakaoId(String kakaoId, Long memberId) {
        if (memberRepository.existsByKakaoIdAndIdNot(kakaoId, memberId)) {
            throw new KakaoIdAlreadyExistsException();
        }
    }

    private Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
    }
}
