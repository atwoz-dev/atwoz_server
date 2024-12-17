package atwoz.atwoz.profileimage.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileImageUploadRequest {
    MultipartFile image;
    Boolean isPrimary;
    Integer order;
}