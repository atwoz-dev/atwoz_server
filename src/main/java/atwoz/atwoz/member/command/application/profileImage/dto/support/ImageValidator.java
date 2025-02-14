package atwoz.atwoz.member.command.application.profileImage.dto.support;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class ImageValidator implements ConstraintValidator<ValidImage, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
        if (multipartFile != null && !multipartFile.getContentType().startsWith("image/")) {
            return false;
        }
        multipartFile.getContentType();
        return true;
    }
}
