package com.fiap.fiap_video_extractor.pkg.interfaces;

import com.fiap.fiap_video_extractor.pkg.dto.NotificationRequest;

public interface NotificationProducer {
    void notifyExtractionComplete(NotificationRequest notificationRequest);
}
