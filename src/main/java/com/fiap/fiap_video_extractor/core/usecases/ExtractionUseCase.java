package com.fiap.fiap_video_extractor.core.usecases;

import com.fiap.fiap_video_extractor.adapters.gateways.ExtractionInfoGateway;
import com.fiap.fiap_video_extractor.adapters.gateways.FileStorageGateway;
import com.fiap.fiap_video_extractor.core.entities.ExtractionInfo;
import com.fiap.fiap_video_extractor.core.entities.ExtractionInfoFile;
import com.fiap.fiap_video_extractor.core.entities.ExtractionStatus;
import com.fiap.fiap_video_extractor.core.requests.ExtractionRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class ExtractionUseCase {

    private final String BUCKET_NAME = "images-extractions";

    private final FileStorageGateway fileStorageGateway;
    private final ExtractionInfoGateway extractionInfoGateway;

    public ExtractionUseCase(FileStorageGateway fileStorageGateway, ExtractionInfoGateway extractionInfoGateway) {
        this.fileStorageGateway = fileStorageGateway;
        this.extractionInfoGateway = extractionInfoGateway;
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
            // send command to queue

            return extractionInfo;
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public ExtractionInfo getExtractionIndo(String userId, String id) {
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

        // Convert the hash to a hexadecimal string
        byte[] hashBytes = digest.digest();
        return HexFormat.of().formatHex(hashBytes);
    }
}
