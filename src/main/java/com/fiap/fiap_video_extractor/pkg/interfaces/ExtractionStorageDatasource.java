package com.fiap.fiap_video_extractor.pkg.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface ExtractionStorageDatasource {
    void uploadFile(String objectKey, MultipartFile file);
}
