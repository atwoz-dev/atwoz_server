package atwoz.atwoz.profileimage.application.dto;

import java.util.ArrayList;
import java.util.List;

public class ProfileImageUploadRequestWrapper {
    private List<ProfileImageUploadRequest> requests = new ArrayList<>();

    public List<ProfileImageUploadRequest> getRequests() {
        return requests;
    }
}