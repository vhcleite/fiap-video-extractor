package com.fiap.fiap_video_extractor.core.usecases;

import com.fiap.fiap_video_extractor.adapters.gateways.ExtractionCommandGateway;
import com.fiap.fiap_video_extractor.adapters.gateways.ExtractionInfoGateway;
import com.fiap.fiap_video_extractor.adapters.gateways.FileStorageGateway;
import com.fiap.fiap_video_extractor.core.entities.*;
import com.fiap.fiap_video_extractor.core.requests.ExtractionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
public class ExtractionUseCase {
    private static final Logger log = LoggerFactory.getLogger(ExtractionUseCase.class);
    private final FileStorageGateway fileStorageGateway;
    private final ExtractionInfoGateway extractionInfoGateway;
    private final ExtractionCommandGateway extractionCommandGateway;
    private final VideoExtractionUseCase videoExtractionUseCase;

    public ExtractionUseCase(
            FileStorageGateway fileStorageGateway,
            ExtractionInfoGateway extractionInfoGateway,
            ExtractionCommandGateway extractionCommandGateway, VideoExtractionUseCase videoExtractionUseCase) {
        this.fileStorageGateway = fileStorageGateway;
        this.extractionInfoGateway = extractionInfoGateway;
        this.extractionCommandGateway = extractionCommandGateway;
        this.videoExtractionUseCase = videoExtractionUseCase;
    }

    public ExtractionInfo extractFile(ExtractionRequest request, MultipartFile file) {
        try {
            UUID id = UUID.randomUUID();
            String objectKey = String.format("%s/uploads/%s", id, file.getOriginalFilename());
            String extractedFilePath = String.format("%s/download/%s.zip", id, id);
            String fileHash = computeFileHash(file.getInputStream());

            var extractionInfo = buildExtractionInfo(request, file, id, objectKey, fileHash, extractedFilePath);

            fileStorageGateway.uploadFile(objectKey, file);
            extractionInfoGateway.save(extractionInfo);
            extractionCommandGateway.sendExtractionCommand(extractionInfo);

            return extractionInfo;
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public ExtractionInfo getExtractionInfo(String userId, String id) {
        return extractionInfoGateway.get(userId, id);
    }

    private static ExtractionInfo buildExtractionInfo(
            ExtractionRequest request,
            MultipartFile file,
            UUID id,
            String objectKey,
            String fileHash,
            String extractedFilePath
    ) {
        return new ExtractionInfo(
                id.toString(),
                request.userId(),
                ExtractionStatus.PENDING,
                objectKey,
                extractedFilePath,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                new ExtractionInfoFile(
                        file.getOriginalFilename(),
                        file.getSize(),
                        fileHash
                )
        );
    }

    private String computeFileHash(InputStream fileStream) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] buffer = new byte[8192];
        int bytesRead;

        while ((bytesRead = fileStream.read(buffer)) != -1) {
            digest.update(buffer, 0, bytesRead);
        }

        byte[] hashBytes = digest.digest();
        return HexFormat.of().formatHex(hashBytes);
    }

    public ExtractionResult getExtractionFile(String userId, String extractionId) {
        ExtractionInfo info = extractionInfoGateway.get(userId, extractionId);

        if (info.status() != ExtractionStatus.COMPLETE) throw new RuntimeException("extraction not ready to download");

        InputStream extractedFile = fileStorageGateway.getFile(info.extractedFilePath());
        return new ExtractionResult(info.extractedFilePath(), extractedFile);
    }

    public void executeExtraction(ExtractionCommand extractionCommand) {
        log.info("query extraction {}", extractionCommand.id());
        var extractionInfo = extractionInfoGateway.get(extractionCommand.userId(), extractionCommand.id());

        if (extractionInfo.status() != ExtractionStatus.PENDING) {
            log.info("ignoring extraction as it already started");
            return;
        }

        extractionInfo = extractionInfo.withStatus(ExtractionStatus.IN_PROGRESS);
        extractionInfoGateway.save(extractionInfo);
        // buscar arquivo
        InputStream videoInputStream = fileStorageGateway.getFile(extractionInfo.videoStoragePath());
        // extrair imagens
        try {
            log.info("starting extraction {}", extractionCommand.id());
            Path zipFile = videoExtractionUseCase.processVideo(extractionInfo.id(), videoInputStream);
            log.info("generated zip file fro extraction {} in {}", extractionInfo, zipFile.toAbsolutePath());
            try {
                fileStorageGateway.uploadFile(extractionInfo.extractedFilePath(), zipFile);
            } finally {
                Files.delete(zipFile);
            }

        } catch (Exception e) {
            log.error("error on extraction {}: {}", extractionInfo.id(), e.getMessage(), e);
            extractionInfo = extractionInfo.withStatus(ExtractionStatus.ERROR);
            extractionInfoGateway.save(extractionInfo);
        }
        // atualizar estado em caso de sucesso
        extractionInfo = extractionInfo.withStatus(ExtractionStatus.COMPLETE);
        extractionInfoGateway.save(extractionInfo);
        log.info("extraction {} completed", extractionInfo.id());
    }

    public List<ExtractionInfo> getExtractionsByUserId(String userId) {
        log.info("query extractions of user {}", userId);
        return extractionInfoGateway.getByUserId(userId);
    }
}
