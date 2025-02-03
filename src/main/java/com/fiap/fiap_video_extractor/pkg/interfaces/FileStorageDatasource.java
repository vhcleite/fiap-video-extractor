package com.fiap.fiap_video_extractor.pkg.interfaces;

import java.io.InputStream;
import java.nio.file.Path;

public interface FileStorageDatasource {
    void uploadFile(String objectKey, Path file);

    InputStream getFile(String objectKey);
}
