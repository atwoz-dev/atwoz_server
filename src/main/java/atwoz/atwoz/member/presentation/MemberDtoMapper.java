package atwoz.atwoz.member.presentation;

import atwoz.atwoz.member.application.dto.MemberLoginResponse;
import atwoz.atwoz.member.application.dto.MemberLoginServiceDto;

public class MemberDtoMapper {
    public static MemberLoginResponse toMemberLoginResponse(MemberLoginServiceDto dto) {
        return MemberLoginResponse.builder()
                .accessToken(dto.accessToken())
                .isProfileSettingNeeded(dto.isProfileSettingNeeded())
                .build();
    }
}
