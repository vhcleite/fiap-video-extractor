package com.fiap.fiap_video_extractor.core.usecases;

import com.fiap.fiap_video_extractor.adapters.gateways.NotificationGateway;
import com.fiap.fiap_video_extractor.core.entities.ExtractionInfo;
import org.springframework.stereotype.Service;

@Service
public class NotificationUseCase {

    private final NotificationGateway notificationGateway;


    public NotificationUseCase(NotificationGateway notificationGateway) {
        this.notificationGateway = notificationGateway;
    }

    public void notifyExtractionComplete(ExtractionInfo extractionInfo) {
        notificationGateway.notifyExtractionComplete(extractionInfo);
    }

    public void notifyExtractionError(ExtractionInfo extractionInfo) {
        notificationGateway.notifyExtractionError(extractionInfo);
    }
}
