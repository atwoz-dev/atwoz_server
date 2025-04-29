package atwoz.atwoz.member.command.application.member;


import atwoz.atwoz.member.presentation.member.MemberMapper;
import atwoz.atwoz.member.presentation.member.dto.MemberProfileUpdateRequest;
import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberProfileService {

    private final MemberCommandRepository memberCommandRepository;


    /**
     * Updates the profile information of a member with the specified ID.
     *
     * @param memberId the unique identifier of the member to update
     * @param request the profile update data to apply to the member
     * @throws MemberNotFoundException if no member exists with the given ID
     */
    @Transactional
    public void updateMember(Long memberId, MemberProfileUpdateRequest request) {
        Member member = getMemberById(memberId);

        member.updateProfile(MemberMapper.toMemberProfile(request));
    }

    /**
     * Marks the specified member as dormant.
     *
     * @param memberId the ID of the member to mark as dormant
     * @throws MemberNotFoundException if no member with the given ID exists
     */
    @Transactional
    public void changeToDormant(Long memberId) {
        getMemberById(memberId).changeToDormant();
    }

    /**
     * Retrieves a member by ID or throws an exception if not found.
     *
     * @param memberId the unique identifier of the member
     * @return the Member entity corresponding to the given ID
     * @throws MemberNotFoundException if no member exists with the specified ID
     */
    private Member getMemberById(Long memberId) {
        return memberCommandRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
    }
}
