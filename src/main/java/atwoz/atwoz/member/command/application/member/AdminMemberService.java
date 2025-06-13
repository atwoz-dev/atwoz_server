package atwoz.atwoz.member.command.application.member;

import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.command.domain.member.ActivityStatus;
import atwoz.atwoz.member.command.domain.member.Grade;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import atwoz.atwoz.member.presentation.member.dto.AdminMemberSettingUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminMemberService {

    private final MemberCommandRepository memberCommandRepository;

    @Transactional
    public void updateMemberSetting(long memberId, AdminMemberSettingUpdateRequest request) {
        Grade grade = Grade.from(request.grade());
        ActivityStatus activityStatus = ActivityStatus.from(request.activityStatus());
        Member member = getMember(memberId);
        member.updateSetting(
            grade,
            activityStatus,
            request.isVip(),
            request.isPushNotificationEnabled()
        );
    }

    private Member getMember(long memberId) {
        return memberCommandRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
    }
}
