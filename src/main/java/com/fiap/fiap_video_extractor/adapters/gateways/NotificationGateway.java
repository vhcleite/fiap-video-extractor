package com.fiap.fiap_video_extractor.adapters.gateways;

import com.fiap.fiap_video_extractor.core.entities.ExtractionInfo;
import com.fiap.fiap_video_extractor.pkg.dto.NotificationRequest;
import com.fiap.fiap_video_extractor.pkg.interfaces.NotificationProducer;
import org.springframework.stereotype.Component;

@Component
public class NotificationGateway {
    private final NotificationProducer producer;

    public NotificationGateway(NotificationProducer producer) {
        this.producer = producer;
    }

    public void notifyExtractionComplete(ExtractionInfo extractionInfo) {
        var request = new NotificationRequest(
                extractionInfo.userId(), extractionInfo.id(), "create"
        );
        producer.notifyExtractionComplete(request);
    }

    public void notifyExtractionError(ExtractionInfo extractionInfo) {
        var request = new NotificationRequest(
                extractionInfo.userId(), extractionInfo.id(), "error"
        );
        producer.notifyExtractionComplete(request);
    }
}
