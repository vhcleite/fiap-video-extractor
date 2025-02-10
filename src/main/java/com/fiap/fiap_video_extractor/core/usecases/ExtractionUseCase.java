package com.fiap.fiap_video_extractor.core.usecases;

import com.fiap.fiap_video_extractor.adapters.gateways.ExtractionInfoGateway;
import com.fiap.fiap_video_extractor.core.entities.ExtractionInfo;
import com.fiap.fiap_video_extractor.core.entities.ExtractionInfoFile;
import com.fiap.fiap_video_extractor.core.entities.ExtractionStatus;
import com.fiap.fiap_video_extractor.core.exceptions.ExtractionAlreadyBeingProcessedException;
import com.fiap.fiap_video_extractor.core.exceptions.InvalidFileExtensionException;
import com.fiap.fiap_video_extractor.core.requests.ExtractionRequest;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
public class ExtractionUseCase {
    private static final Logger log = LoggerFactory.getLogger(ExtractionUseCase.class);

    private final ExtractionInfoGateway extractionInfoGateway;
    private final Tika tika;

    public ExtractionUseCase(ExtractionInfoGateway extractionInfoGateway, Tika tika) {
        this.extractionInfoGateway = extractionInfoGateway;
        this.tika = tika;
    }

    public ExtractionInfo createExtraction(ExtractionRequest request, MultipartFile file) {
        try {
            validateFileExtension(file);

            UUID id = UUID.randomUUID();
            String objectKey = String.format("%s/uploads/%s", id, file.getOriginalFilename());
            String extractedFilePath = String.format("%s/download/%s.zip", id, id);
            String fileHash = computeFileHash(file.getInputStream());

            var extractionInfo = buildExtractionInfo(request, file, id, objectKey, fileHash, extractedFilePath);
            return this.extractionInfoGateway.save(extractionInfo);
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void validateFileExtension(MultipartFile file) throws IOException {
        String detectedType = tika.detect(file.getInputStream());
        if (!List.of("video/mp4", "video/x-m4v", "video/quicktime").contains(detectedType)) {
            throw new InvalidFileExtensionException(String.format("file type %s not allowed", detectedType));
        }
    }

    public ExtractionInfo getExtractionInfo(String userId, String id) {
        return extractionInfoGateway.get(userId, id);
    }

    public List<ExtractionInfo> getExtractionsByUserId(String userId) {
        return extractionInfoGateway.getByUserId(userId);
    }

    public ExtractionInfo startExtraction(String userId, String id) {
        log.info("query extraction {}", id);
        var extractionInfo = this.getExtractionInfo(userId, id);

        if (extractionInfo.status() != ExtractionStatus.PENDING) {
            throw new ExtractionAlreadyBeingProcessedException("extraction already being processed");
        }

        extractionInfo = extractionInfo.withStatus(ExtractionStatus.IN_PROGRESS);
        extractionInfoGateway.save(extractionInfo);
        return extractionInfo;
    }

    public ExtractionInfo updateStatus(String userId, String id, ExtractionStatus extractionStatus) {
        ExtractionInfo persistedExtractionInfo = extractionInfoGateway.get(userId, id);
        ExtractionInfo newExtractionInfo = persistedExtractionInfo.withStatus(extractionStatus);

        return extractionInfoGateway.save(newExtractionInfo);
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
}
