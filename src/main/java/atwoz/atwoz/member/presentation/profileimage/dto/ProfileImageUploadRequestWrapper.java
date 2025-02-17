package atwoz.atwoz.member.presentation.profileimage.dto;

import jakarta.validation.Valid;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ProfileImageUploadRequestWrapper {
    @Valid
    private final List<ProfileImageUploadRequest> requests = new ArrayList<>();
}