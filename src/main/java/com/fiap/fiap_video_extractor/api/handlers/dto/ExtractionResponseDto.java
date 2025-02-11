package com.fiap.fiap_video_extractor.api.handlers.dto;

record ExtractionResponseOriginalFileDto(
        String hash,
        String extension,
        String name,
        Long sizeBytes
) {
}

public record ExtractionResponseDto(
        String id,
        String userId,
        ExtractionResponseOriginalFileDto originalFile
) {
}
