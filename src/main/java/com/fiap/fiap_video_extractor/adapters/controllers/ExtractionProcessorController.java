package com.fiap.fiap_video_extractor.adapters.controllers;

import com.fiap.fiap_video_extractor.core.entities.ExtractionCommand;
import com.fiap.fiap_video_extractor.core.entities.ExtractionInfo;
import com.fiap.fiap_video_extractor.core.entities.ExtractionStatus;
import com.fiap.fiap_video_extractor.core.usecases.ExtractionUseCase;
import com.fiap.fiap_video_extractor.core.usecases.FileStorageUseCase;
import com.fiap.fiap_video_extractor.core.usecases.VideoProcessorUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class ExtractionProcessorController {

    private static final Logger log = LoggerFactory.getLogger(ExtractionProcessorController.class);

    private final ExtractionUseCase extractionUseCase;
    private final FileStorageUseCase fileStorageUseCase;
    private final VideoProcessorUseCase videoProcessorUseCase;

    public ExtractionProcessorController(
            ExtractionUseCase extractionUseCase,
            FileStorageUseCase fileStorageUseCase,
            VideoProcessorUseCase videoProcessorUseCase) {
        this.extractionUseCase = extractionUseCase;
        this.fileStorageUseCase = fileStorageUseCase;
        this.videoProcessorUseCase = videoProcessorUseCase;
    }

    public ExtractionInfo executeExtraction(ExtractionCommand extractionCommand) {
        var extractionInfo = extractionUseCase.startExtraction(extractionCommand.userId(), extractionCommand.id());
        try {
            InputStream videoInputStream = fileStorageUseCase.getFile(extractionInfo.videoStoragePath());

            log.info("starting extraction {}", extractionCommand.id());
            Path zipFile = videoProcessorUseCase.process(extractionInfo.id(), videoInputStream);
            log.info("generated zip file from extraction {} in {}", extractionInfo, zipFile.toAbsolutePath());
            try {
                fileStorageUseCase.uploadFile(extractionInfo.extractedFilePath(), zipFile);
            } finally {
                Files.delete(zipFile);
            }

        } catch (Exception e) {
            log.error("error on extraction {}: {}", extractionInfo.id(), e.getMessage(), e);
            return extractionUseCase.updateStatus(extractionInfo.userId(), extractionInfo.id(), ExtractionStatus.ERROR);
        }
        return extractionUseCase.updateStatus(extractionInfo.userId(), extractionInfo.id(), ExtractionStatus.COMPLETE);
    }
}
