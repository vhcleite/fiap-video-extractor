package com.fiap.fiap_video_extractor.pkg.interfaces;

import com.fiap.fiap_video_extractor.core.entities.ExtractionInfo;

public interface ExtractionCommandProducer {
    void sendExtractionInfo(ExtractionInfo extractionInfo);
}
