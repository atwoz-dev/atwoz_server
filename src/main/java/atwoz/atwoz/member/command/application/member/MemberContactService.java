package atwoz.atwoz.member.command.application.member;

import atwoz.atwoz.member.query.member.dto.MemberContactResponse;
import atwoz.atwoz.member.command.application.member.exception.KakaoIdAlreadyExistsException;
import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.command.application.member.exception.PhoneNumberAlreadyExistsException;
import atwoz.atwoz.member.command.domain.member.vo.KakaoId;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberContactService {

    private final MemberCommandRepository memberCommandRepository;

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
        if (memberCommandRepository.existsByPhoneNumberAndIdNot(phoneNumber, memberId)) {
            throw new PhoneNumberAlreadyExistsException();
        }
    }

    private void validateKakaoId(String kakaoId, Long memberId) {
        if (memberCommandRepository.existsByKakaoIdAndIdNot(kakaoId, memberId)) {
            throw new KakaoIdAlreadyExistsException();
        }
    }

    private Member getMemberById(Long memberId) {
        return memberCommandRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
    }
}
