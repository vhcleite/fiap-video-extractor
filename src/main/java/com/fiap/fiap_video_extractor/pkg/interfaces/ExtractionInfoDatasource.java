package com.fiap.fiap_video_extractor.pkg.interfaces;

import com.fiap.fiap_video_extractor.core.entities.ExtractionInfo;

public interface ExtractionInfoDatasource {
    ExtractionInfo save(ExtractionInfo info);

    ExtractionInfo get(String userId, String id);
}
