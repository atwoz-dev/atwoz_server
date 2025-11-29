package deepple.deepple.member.presentation.member;

import deepple.deepple.member.command.application.member.dto.MemberLoginServiceDto;
import deepple.deepple.member.presentation.member.dto.MemberLoginResponse;

public class MemberDtoMapper {
    public static MemberLoginResponse toMemberLoginResponse(MemberLoginServiceDto dto) {
        return new MemberLoginResponse(dto.accessToken(), dto.isProfileSettingNeeded(), dto.activityStatus());
    }
}
