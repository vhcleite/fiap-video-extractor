package com.fiap.fiap_video_extractor.external;

import com.fiap.fiap_video_extractor.pkg.interfaces.FileStorageDatasource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.nio.file.Path;

@Component
public class AwsS3Datasource implements FileStorageDatasource {

    Logger logger = LoggerFactory.getLogger(AwsS3Datasource.class);

    @Value("${config.aws.s3-buckets.file-storage}")
    private String bucketName;

    private final S3Client s3Client;

    public AwsS3Datasource(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void uploadFile(String objectKey, Path file) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        s3Client.putObject(putObjectRequest, file);
        logger.info("file {} sent to s3", file.getFileName());
    }

    @Override
    public InputStream getFile(String objectKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        return s3Client.getObject(getObjectRequest);
    }
}
