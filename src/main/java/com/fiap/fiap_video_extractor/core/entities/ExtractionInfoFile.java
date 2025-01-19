package com.fiap.fiap_video_extractor.core.entities;

import java.time.OffsetDateTime;

public record ExtractionInfoFile(
        String hash,
        String extension,
        String name,
        Long sizeBytes,
        String videoStoragePath,
        String extractedFilePath,
        ExtractionStatus extractedStatus,
        OffsetDateTime uploadAt,
        OffsetDateTime updatedAt
) {
}