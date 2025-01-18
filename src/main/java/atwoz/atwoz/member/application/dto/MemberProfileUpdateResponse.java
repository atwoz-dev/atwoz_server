package atwoz.atwoz.member.application.dto;

import atwoz.atwoz.member.domain.member.MemberProfile;

public record MemberProfileUpdateResponse(
        MemberProfile memberProfile
) {
}
