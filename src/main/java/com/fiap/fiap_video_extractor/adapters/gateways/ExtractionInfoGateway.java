package com.fiap.fiap_video_extractor.adapters.gateways;

import com.fiap.fiap_video_extractor.core.entities.ExtractionInfo;
import com.fiap.fiap_video_extractor.pkg.interfaces.ExtractionInfoDatasource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExtractionInfoGateway {
    private final ExtractionInfoDatasource extractionInfoDatasource;

    public ExtractionInfoGateway(ExtractionInfoDatasource extractionInfoDatasource) {
        this.extractionInfoDatasource = extractionInfoDatasource;
    }

    public ExtractionInfo save(ExtractionInfo info) {
        return extractionInfoDatasource.save(info);
    }

    public ExtractionInfo get(String userId, String id) {
        return extractionInfoDatasource.get(userId, id);
    }

    public List<ExtractionInfo> getByUserId(String userId) {
        return extractionInfoDatasource.getByUserId(userId);
    }
}
