package com.fiap.fiap_video_extractor.adapters.gateways;

import com.fiap.fiap_video_extractor.pkg.interfaces.FileStorageDatasource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Path;

@Component
public class FileStorageGateway {
    private final FileStorageDatasource fileStorageDatasource;

    public FileStorageGateway(FileStorageDatasource fileStorageDatasource) {
        this.fileStorageDatasource = fileStorageDatasource;
    }

    public void uploadFile(String objectKey, Path file) {
        fileStorageDatasource.uploadFile(objectKey, file);
    }

    public InputStream getFile(String objectKey) {
        return fileStorageDatasource.getFile(objectKey);
    }
}
