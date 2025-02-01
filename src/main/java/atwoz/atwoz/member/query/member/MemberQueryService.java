package atwoz.atwoz.member.query.member;

import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.query.member.dto.MemberContactResponse;
import atwoz.atwoz.member.query.member.dto.MemberProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberQueryService {
    private final MemberQueryRepository memberQueryRepository;

    public MemberProfileResponse getProfile(Long memberId) {
        return memberQueryRepository.findProfileByMemberId(memberId)
                .orElseThrow(() -> new MemberNotFoundException());
    }

    public MemberContactResponse getContacts(Long memberId) {
        return memberQueryRepository.findContactsByMemberId(memberId)
                .orElseThrow(() -> new MemberNotFoundException());
    }
}
