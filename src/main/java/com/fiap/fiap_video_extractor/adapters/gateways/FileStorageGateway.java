package com.fiap.fiap_video_extractor.adapters.gateways;

import com.fiap.fiap_video_extractor.pkg.interfaces.FileStorageDatasource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Component
public class FileStorageGateway {
    private final FileStorageDatasource fileStorageDatasource;

    public FileStorageGateway(FileStorageDatasource fileStorageDatasource) {
        this.fileStorageDatasource = fileStorageDatasource;
    }

    public void uploadFile(String objectKey, MultipartFile file) {
        try {
            // Create a temporary file to upload
            Path tempFile = Files.createTempFile("upload-", file.getOriginalFilename());
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
            try {
                fileStorageDatasource.uploadFile(objectKey, tempFile);
            } finally {
                Files.delete(tempFile); // Clean up the temporary file
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void uploadFile(String objectKey, Path file) {
        fileStorageDatasource.uploadFile(objectKey, file);
    }

    public InputStream getFile(String objectKey) {
        return fileStorageDatasource.getFile(objectKey);
    }
}
