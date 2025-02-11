package com.fiap.fiap_video_extractor.adapters.gateways;

import com.fiap.fiap_video_extractor.core.entities.ExtractionCommand;
import com.fiap.fiap_video_extractor.core.entities.ExtractionInfo;
import com.fiap.fiap_video_extractor.pkg.interfaces.ExtractionCommandProducer;
import org.springframework.stereotype.Component;

@Component
public class ExtractionCommandGateway {
    private final ExtractionCommandProducer producer;

    public ExtractionCommandGateway(ExtractionCommandProducer producer) {
        this.producer = producer;
    }

    public void sendExtractionCommand(ExtractionInfo extractionInfo) {
        producer.sendExtractionInfo(new ExtractionCommand(extractionInfo.id(), extractionInfo.userId()));
    }
}
