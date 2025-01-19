package com.fiap.fiap_video_extractor.external;

import com.fiap.fiap_video_extractor.pkg.interfaces.ExtractionStorageDatasource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Component
public class AwsS3Datasource implements ExtractionStorageDatasource {

    private final String BUCKET_NAME = "images-extractions";

    private final S3Client s3Client;


    public AwsS3Datasource(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void uploadFile(String objectKey, MultipartFile file) {
        try {
            // Create a temporary file to upload
            Path tempFile = Files.createTempFile("upload-", file.getOriginalFilename());
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            // Upload the file to S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(objectKey)
                    .build();

            s3Client.putObject(putObjectRequest, tempFile);

            Files.delete(tempFile); // Clean up the temporary file
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
