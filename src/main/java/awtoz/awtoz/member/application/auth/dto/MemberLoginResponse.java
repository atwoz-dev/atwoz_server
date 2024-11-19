package awtoz.awtoz.member.application.auth.dto;

import awtoz.awtoz.member.domain.member.ActivityStatus;
import awtoz.awtoz.member.domain.member.Member;
import lombok.Builder;

@Builder
public record MemberLoginResponse(
        String accessToken,
        String refreshToken,
        boolean isSuspended,
        boolean isNeedProfile
) {
    public static MemberLoginResponse fromMemberWithToken(Member member,String accessToken, String refreshToken, boolean isDuplicated) {
        if (member.getActivityStatus().equals(ActivityStatus.PERMANENT_STOP)) {
            return MemberLoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .isSuspended(member.getActivityStatus().isPermanentStop())
                    .isNeedProfile(isDuplicated)
                    .build();
        }

        else
            return MemberLoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .isSuspended(member.getActivityStatus().isPermanentStop())
                    .isNeedProfile(isDuplicated)
                    .build();

    }
}
