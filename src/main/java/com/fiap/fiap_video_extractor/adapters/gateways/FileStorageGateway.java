package com.fiap.fiap_video_extractor.adapters.gateways;

import com.fiap.fiap_video_extractor.pkg.interfaces.ExtractionStorageDatasource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileStorageGateway {
    private final ExtractionStorageDatasource extractionStorageDatasource;

    public FileStorageGateway(ExtractionStorageDatasource extractionStorageDatasource) {
        this.extractionStorageDatasource = extractionStorageDatasource;
    }

    public void uploadFile(String objectKey, MultipartFile file) {
        extractionStorageDatasource.uploadFile(objectKey, file);
    }
}
