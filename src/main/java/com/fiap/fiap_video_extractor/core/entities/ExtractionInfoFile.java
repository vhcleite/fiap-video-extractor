package com.fiap.fiap_video_extractor.core.entities;

public record ExtractionInfoFile(
        String name,
        Long sizeBytes,
        String hash
) {
}