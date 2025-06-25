package atwoz.atwoz.member.command.application.member;


import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.command.application.member.exception.PrimaryContactTypeSettingNeededException;
import atwoz.atwoz.member.command.domain.member.ActivityStatus;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import atwoz.atwoz.member.command.domain.member.PrimaryContactType;
import atwoz.atwoz.member.presentation.member.MemberMapper;
import atwoz.atwoz.member.presentation.member.dto.MemberProfileUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberProfileService {

    private final MemberCommandRepository memberCommandRepository;


    @Transactional
    public void updateMember(Long memberId, MemberProfileUpdateRequest request) {
        Member member = getMemberById(memberId);

        member.updateProfile(MemberMapper.toMemberProfile(request));
    }

    @Transactional
    public void changeToDormant(Long memberId) {
        getMemberById(memberId).changeToDormant();
    }

    @Transactional
    public void validatePrimaryContactTypeSetting(Long memberId) {
        if (getMemberById(memberId).getPrimaryContactType() == PrimaryContactType.NONE) {
            throw new PrimaryContactTypeSettingNeededException();
        }
    }

    @Transactional
    public void changeProfilePublishStatus(Long memberId, boolean publishStatus) {
        Member member = getMemberById(memberId);
        if (publishStatus) {
            publish(member);
        } else {
            nonPublish(member);
        }
    }

    @Transactional
    public void changeMemberActivityStatus(Long memberId, ActivityStatus status) {
        Member member = getMemberById(memberId);
        member.changeActivityStatus(status);

        if (status != ActivityStatus.ACTIVE) {
            nonPublish(member);
        } else {
            publish(member);
        }
    }

    private void publish(Member member) {
        member.publishProfile();
    }

    private void nonPublish(Member member) {
        member.nonPublishProfile();
    }

    private Member getMemberById(Long memberId) {
        return memberCommandRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
    }
}
