package com.fiap.fiap_video_extractor.pkg.dto;

public record NotificationRequest(
        String userId,
        String extractionId,
        String type,
        String email
) {
}
