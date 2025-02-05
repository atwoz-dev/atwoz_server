package atwoz.atwoz.member.command.application.profileImage.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ProfileImageUpdateRequestWrapper {
    List<ProfileImageUpdateRequest> requests = new ArrayList<>();
}
