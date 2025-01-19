package com.fiap.fiap_video_extractor.core.usecases;

import com.fiap.fiap_video_extractor.adapters.gateways.FileStorageGateway;
import com.fiap.fiap_video_extractor.core.entities.ExtractionInfo;
import com.fiap.fiap_video_extractor.core.entities.ExtractionInfoFile;
import com.fiap.fiap_video_extractor.core.entities.ExtractionStatus;
import com.fiap.fiap_video_extractor.core.requests.ExtractionRequest;
import org.apache.commons.io.FilenameUtils;
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

    public ExtractionUseCase(FileStorageGateway fileStorageGateway) {
        this.fileStorageGateway = fileStorageGateway;
    }

    public ExtractionInfo extractFile(ExtractionRequest request, MultipartFile file) {
        try {
            UUID id = UUID.randomUUID();
            String objectKey = String.format("%s/uploads/%s", id, file.getOriginalFilename());
            String fileHash = computeFileHash(file.getInputStream());
            var extractionInfp = new ExtractionInfo(
                    id.toString(),
                    request.userId(),
                    new ExtractionInfoFile(
                            fileHash,
                            FilenameUtils.getExtension(file.getOriginalFilename()),
                            file.getOriginalFilename(),
                            file.getSize(),
                            objectKey,
                            null,
                            ExtractionStatus.PENDING,
                            OffsetDateTime.now(),
                            OffsetDateTime.now()
                    )
            );

            fileStorageGateway.uploadFile(objectKey, file);
            
            // persist info in database
            // send command to queue

            return extractionInfp;
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
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
