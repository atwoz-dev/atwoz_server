package atwoz.atwoz.member.command.infra.profileImage;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
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

    public String getPreSignedUrl(String fileName) {
        Date expiration = new Date(
            System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(PRESIGNED_URL_EXPIRATION_MINUTES));
        return s3Client.generatePresignedUrl(bucket, generateUniqueKey(fileName), expiration).toString();
    }

    private String generateUniqueKey(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf("."));
        return UUID.randomUUID() + extension;
    }
}
