package atwoz.atwoz.profileimage.application.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProfileImageUploadRequest {
    MultipartFile image;
    Boolean isPrimary;
    Integer order;
}