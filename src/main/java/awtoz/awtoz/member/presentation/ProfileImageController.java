package awtoz.awtoz.member.presentation;

import awtoz.awtoz.member.application.ProfileImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class ProfileImageController {

    private final ProfileImageService profileImageService;

    @PostMapping("/image")
    public ResponseEntity<String> uploadProfileImage(@RequestParam("file") MultipartFile image) {
        String url = profileImageService.uploadImage(image);
        return new ResponseEntity<>(url, HttpStatus.CREATED);
    }

    @DeleteMapping("/image")
    public ResponseEntity deleteProfileImage(@RequestBody String imageUrl) {
        profileImageService.deleteImage(imageUrl);
        return ResponseEntity.ok(null);
    }

}
