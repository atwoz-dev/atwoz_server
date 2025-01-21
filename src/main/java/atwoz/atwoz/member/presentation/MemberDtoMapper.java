package atwoz.atwoz.member.presentation;

import atwoz.atwoz.member.command.application.dto.MemberLoginResponse;
import atwoz.atwoz.member.command.application.dto.MemberLoginServiceDto;

public class MemberDtoMapper {
    public static MemberLoginResponse toMemberLoginResponse(MemberLoginServiceDto dto) {
        return MemberLoginResponse.builder()
                .accessToken(dto.accessToken())
                .isProfileSettingNeeded(dto.isProfileSettingNeeded())
                .build();
    }
}
