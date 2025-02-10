package com.fiap.fiap_video_extractor.pkg.interfaces;

import com.fiap.fiap_video_extractor.core.entities.ExtractionCommand;

public interface ExtractionCommandProducer {
    void sendExtractionInfo(ExtractionCommand extractionCommand);
}
