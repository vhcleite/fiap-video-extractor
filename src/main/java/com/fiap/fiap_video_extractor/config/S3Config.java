package com.fiap.fiap_video_extractor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class S3Config {

    @Bean
    @Profile("!local")
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.US_EAST_1) // Change to your bucket's region
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("AWS_ACCESS_KEY", "AWS_SECRET_KEY")
                ))
                .build();
    }

    @Bean
    @Profile("local")
    public S3Client s3ClientLocal() {
        return S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("localstack", "localstack")
                ))
                .endpointOverride(URI.create("http://127.0.0.1:4566"))
                .build();
    }
}
