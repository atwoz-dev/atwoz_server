package awtoz.awtoz.member.application;

import awtoz.awtoz.global.config.S3Config;
import awtoz.awtoz.member.domain.ProfileImage;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileImageService {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.bucket}")
    private String bucket;

    public String uploadImage(MultipartFile file) {
        String fileName = UUID.randomUUID().toString();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        /**
         * TODO : Error Handling 필요.
         */
        try {
            amazonS3Client.putObject(bucket, fileName, file.getInputStream(), objectMetadata);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("이미지 업로드 실패");
        }

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    public void deleteImage(String imageUrl) {
        try {
            String imageFileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

            System.out.println(imageFileName);
            amazonS3Client.deleteObject(bucket, imageFileName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("이미지 삭제 실패");
        }
    }
}
