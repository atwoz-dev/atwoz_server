package atwoz.atwoz.member.command.infra.profileImage;

import atwoz.atwoz.member.command.application.profileImage.exception.FileUploadFailException;
import atwoz.atwoz.member.command.infra.profileImage.exception.S3AmazonException;
import atwoz.atwoz.member.command.infra.profileImage.exception.S3ClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
public class S3Uploader {
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.bucket}")
    private String bucket;

    private AmazonS3Client s3Client;

    private String prefixUrl;

    @PostConstruct
    public void init() {
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKey, secretKey);
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setConnectionTimeout(5000);
        clientConfiguration.setSocketTimeout(30000);

        s3Client = (AmazonS3Client) AmazonS3ClientBuilder.standard()
            .withRegion(region)
            .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
            .withClientConfiguration(clientConfiguration)
            .build();

        prefixUrl = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/";
    }

    public String uploadFile(MultipartFile file) {
        String fileName = generateRandomFileName(file);
        ObjectMetadata objectMetadata = getObjectMetadata(file);

        try {
            s3Client.putObject(bucket, fileName, file.getInputStream(), objectMetadata);
        } catch (IOException e) {
            throw new FileUploadFailException(e);
        } catch (AmazonServiceException e) {
            throw new S3AmazonException(e);
        } catch (SdkClientException e) {
            throw new S3ClientException(e);
        }

        return prefixUrl + fileName;
    }

    @Async
    public CompletableFuture<String> uploadImageAsync(MultipartFile image) {
        if (image == null) {
            return CompletableFuture.completedFuture(null);
        }
        String imageUrl = uploadFile(image);
        return CompletableFuture.completedFuture(imageUrl);
    }

    @Async
    public void deleteFile(String url) {
        String key = getKey(url);
        s3Client.deleteObject(bucket, key);
    }

    private String getKey(String url) {
        return url.substring(prefixUrl.length());
    }

    private ObjectMetadata getObjectMetadata(MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();

        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        return objectMetadata;
    }

    private String generateRandomFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return UUID.randomUUID() + extension;
    }
}
