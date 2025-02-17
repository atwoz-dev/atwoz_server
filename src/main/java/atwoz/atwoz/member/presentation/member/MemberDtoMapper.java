package atwoz.atwoz.member.presentation.member;

import atwoz.atwoz.member.presentation.member.dto.MemberLoginResponse;
import atwoz.atwoz.member.command.application.member.dto.MemberLoginServiceDto;

public class MemberDtoMapper {
    public static MemberLoginResponse toMemberLoginResponse(MemberLoginServiceDto dto) {
        return new MemberLoginResponse(dto.accessToken(), dto.isProfileSettingNeeded());
    }
}
