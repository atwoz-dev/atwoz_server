package atwoz.atwoz.profileimage.application.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ProfileImageUploadRequestWrapper {
    private final List<ProfileImageUploadRequest> requests = new ArrayList<>();
}