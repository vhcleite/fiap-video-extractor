package com.fiap.fiap_video_extractor.core.usecases;

import com.fiap.fiap_video_extractor.adapters.gateways.FileStorageGateway;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageUseCase {
    private final FileStorageGateway fileStorageGateway;

    public FileStorageUseCase(FileStorageGateway fileStorageGateway) {
        this.fileStorageGateway = fileStorageGateway;
    }

    public InputStream getFile(String filePath) {
        return fileStorageGateway.getFile(filePath);
    }

    public void uploadFile(String filePath, Path file) {
        fileStorageGateway.uploadFile(filePath, file);
    }

    public void uploadFile(String objectKey, MultipartFile file) {
        try {
            // Create a temporary file to upload
            Path tempFile = Files.createTempFile("upload-", file.getOriginalFilename());
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
            try {
                fileStorageGateway.uploadFile(objectKey, tempFile);
            } finally {
                Files.delete(tempFile); // Clean up the temporary file
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
