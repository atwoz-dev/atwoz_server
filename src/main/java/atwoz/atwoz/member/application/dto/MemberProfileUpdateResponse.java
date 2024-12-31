package atwoz.atwoz.member.application.dto;

import atwoz.atwoz.member.domain.member.vo.MemberProfile;

public record MemberProfileUpdateResponse(
        MemberProfile memberProfile
) {
}
