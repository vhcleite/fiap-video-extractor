package com.fiap.fiap_video_extractor.adapters.controllers;

import com.fiap.fiap_video_extractor.core.entities.ExtractionInfo;
import com.fiap.fiap_video_extractor.core.entities.ExtractionResult;
import com.fiap.fiap_video_extractor.core.entities.ExtractionStatus;
import com.fiap.fiap_video_extractor.core.requests.ExtractionRequest;
import com.fiap.fiap_video_extractor.core.usecases.ExtractionUseCase;
import com.fiap.fiap_video_extractor.core.usecases.FileStorageUseCase;
import com.fiap.fiap_video_extractor.core.usecases.VideoProcessorUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Comparator;
import java.util.List;

@Controller
public class ExtractionController {

    private static final Logger log = LoggerFactory.getLogger(ExtractionController.class);
    private final ExtractionUseCase extractionUseCase;
    private final FileStorageUseCase fileStorageUseCase;
    private final VideoProcessorUseCase videoProcessorUseCase;

    public ExtractionController(
            ExtractionUseCase extractionUseCase,
            FileStorageUseCase fileStorageUseCase,
            VideoProcessorUseCase videoProcessorUseCase) {
        this.extractionUseCase = extractionUseCase;
        this.fileStorageUseCase = fileStorageUseCase;
        this.videoProcessorUseCase = videoProcessorUseCase;
    }

    public ExtractionInfo createExtraction(ExtractionRequest request, MultipartFile file) {
        ExtractionInfo extractionInfo = extractionUseCase.createExtraction(request, file);
        fileStorageUseCase.uploadFile(extractionInfo.videoStoragePath(), file);
        videoProcessorUseCase.asyncProcessVideo(extractionInfo);
        return extractionInfo;
    }

    public ExtractionInfo getExtractionInfo(String userId, String id) {
        return extractionUseCase.getExtractionInfo(userId, id);
    }

    public List<ExtractionInfo> getExtractionsByUserId(String userId) {
        var extractions = extractionUseCase.getExtractionsByUserId(userId);
        extractions.sort(Comparator.comparing(ExtractionInfo::uploadAt).reversed());
        return extractions;
    }

    public ExtractionResult getExtractionFile(String userId, String extractionId) {
        ExtractionInfo info = extractionUseCase.getExtractionInfo(userId, extractionId);

        if (info.status() != ExtractionStatus.COMPLETE) throw new RuntimeException("extraction not ready to download");

        InputStream extractedFile = fileStorageUseCase.getFile(info.extractedFilePath());
        return new ExtractionResult(info.extractedFilePath(), extractedFile);
    }
}
