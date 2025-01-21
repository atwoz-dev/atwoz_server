package atwoz.atwoz.member.command.application.dto;

import atwoz.atwoz.member.command.domain.member.MemberProfile;

public record MemberProfileUpdateResponse(
        MemberProfile memberProfile
) {
}
