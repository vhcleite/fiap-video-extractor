package com.fiap.fiap_video_extractor.core.entities;

public record ExtractionInfo(
        String id,
        String userId,
        ExtractionInfoFile originalFile
) {
}
