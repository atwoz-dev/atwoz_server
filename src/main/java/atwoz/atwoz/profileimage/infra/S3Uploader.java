package atwoz.atwoz.profileimage.infra;

import atwoz.atwoz.profileimage.exception.FIleUploadFailException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

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
    }

    public String uploadFile(MultipartFile file) {
        String fileName = generateRandomFileName(file);
        ObjectMetadata objectMetadata = getObjectMetadata(file);

        try {
            s3Client.putObject(bucket, fileName, file.getInputStream(), objectMetadata);
            return fileName;

        } catch (Exception e) {
            throw new FIleUploadFailException();
        }
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
