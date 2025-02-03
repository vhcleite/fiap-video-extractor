package com.fiap.fiap_video_extractor.core.entities;

import java.io.InputStream;

public record ExtractionResult(
        String fileName,
        InputStream inputStream
) {
}
