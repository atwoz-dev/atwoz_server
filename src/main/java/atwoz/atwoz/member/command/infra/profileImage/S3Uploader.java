package atwoz.atwoz.member.command.infra.profileImage;

import atwoz.atwoz.member.command.infra.profileImage.dto.PresignedUrlResponse;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class S3Uploader {
    private static final int PRESIGNED_URL_EXPIRATION_MINUTES = 10;
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

    public PresignedUrlResponse getPreSignedUrl(String fileName, Long userId) {
        String key = generateUniqueKey(fileName, userId);
        Date expiration = new Date(
            System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(PRESIGNED_URL_EXPIRATION_MINUTES));

        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucket, key)
            .withMethod(HttpMethod.PUT)
            .withExpiration(expiration);

        String presignedUrl = s3Client.generatePresignedUrl(req).toString();
        String objectUrl = prefixUrl + key;

        return new PresignedUrlResponse(presignedUrl, objectUrl);
    }

    private String generateUniqueKey(String fileName, Long userId) {
        String extension = fileName.substring(fileName.lastIndexOf("."));
        return userId + "/" + UUID.randomUUID() + extension;
    }
}
