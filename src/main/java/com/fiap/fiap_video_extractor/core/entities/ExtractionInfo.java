package com.fiap.fiap_video_extractor.core.entities;

import java.time.OffsetDateTime;

public record ExtractionInfo(
        String id,
        String userId,
        String email,
        ExtractionStatus status,
        String videoStoragePath,
        String extractedFilePath,
        OffsetDateTime uploadAt,
        OffsetDateTime updatedAt,
        ExtractionInfoFile originalFile
) {
    public ExtractionInfo withStatus(ExtractionStatus newStatus) {
        return new ExtractionInfo(
                id,
                userId,
                email,
                newStatus,
                videoStoragePath,
                extractedFilePath,
                uploadAt,
                OffsetDateTime.now(),
                originalFile
        );
    }
}
