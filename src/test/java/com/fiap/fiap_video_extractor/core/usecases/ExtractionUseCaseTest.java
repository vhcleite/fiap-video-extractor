package com.fiap.fiap_video_extractor.core.usecases;

import com.fiap.fiap_video_extractor.adapters.gateways.ExtractionInfoGateway;
import com.fiap.fiap_video_extractor.core.entities.ExtractionInfo;
import com.fiap.fiap_video_extractor.core.entities.ExtractionInfoFile;
import com.fiap.fiap_video_extractor.core.entities.ExtractionStatus;
import com.fiap.fiap_video_extractor.core.exceptions.ExtractionAlreadyBeingProcessedException;
import com.fiap.fiap_video_extractor.core.exceptions.InvalidFileExtensionException;
import com.fiap.fiap_video_extractor.core.requests.ExtractionRequest;
import org.apache.tika.Tika;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExtractionUseCaseTest {

    @Mock
    private ExtractionInfoGateway extractionInfoGateway;

    @Mock
    private Tika tika;

    @InjectMocks
    private ExtractionUseCase extractionUseCase;

    @Test
    public void testCreateExtraction() throws Exception {
        // Arrange
        ExtractionRequest request = new ExtractionRequest("user", "email@email.com");

        String fileContent = "Test file content";
        byte[] fileBytes = fileContent.getBytes(StandardCharsets.UTF_8);
        String originalFilename = "test.mp4";
        MultipartFile multipartFile = new MockMultipartFile("file", originalFilename, "video/mp4", fileBytes);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(fileBytes);
        String expectedHash = java.util.HexFormat.of().formatHex(hashBytes);

        when(tika.detect(any(InputStream.class))).thenReturn("video/mp4");
        when(extractionInfoGateway.save(any(ExtractionInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ExtractionInfo result = extractionUseCase.createExtraction(request, multipartFile);

        // Assert
        assertNotNull(result);
        assertEquals(request.userId(), result.userId());
        assertEquals(ExtractionStatus.PENDING, result.status());
        assertEquals(originalFilename, result.originalFile().name());
        assertEquals(multipartFile.getSize(), result.originalFile().sizeBytes());
        assertEquals(expectedHash, result.originalFile().hash());

        verify(extractionInfoGateway, times(1)).save(any());
    }

    @Test
    public void testCreateExtractionErrorByFileType() throws Exception {
        // Arrange
        ExtractionRequest request = new ExtractionRequest("user", "email@email.com");

        String fileContent = "Test file content";
        byte[] fileBytes = fileContent.getBytes(StandardCharsets.UTF_8);
        String originalFilename = "test.mp4";
        MultipartFile multipartFile = new MockMultipartFile("file", originalFilename, "video/mp4", fileBytes);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(fileBytes);
        String expectedHash = java.util.HexFormat.of().formatHex(hashBytes);

        when(tika.detect(any(InputStream.class))).thenReturn("plain/text");

        // Act
        assertThrows(InvalidFileExtensionException.class, () ->
                extractionUseCase.createExtraction(request, multipartFile));


        verify(extractionInfoGateway, never()).save(any());
    }

    @Test
    public void testGetExtractionInfo() {
        // Arrange
        String userId = "user123";
        String extractionId = UUID.randomUUID().toString();
        ExtractionInfo dummyExtraction = createDummyExtraction(extractionId, userId);
        when(extractionInfoGateway.get(userId, extractionId)).thenReturn(dummyExtraction);

        // Act
        ExtractionInfo result = extractionUseCase.getExtractionInfo(userId, extractionId);

        // Assert
        assertNotNull(result);
        assertEquals(dummyExtraction, result);
        verify(extractionInfoGateway).get(userId, extractionId);
    }

    @Test
    public void testGetExtractionsByUserId() {
        // Arrange
        String userId = "user123";
        List<ExtractionInfo> dummyList = List.of(
                createDummyExtraction(UUID.randomUUID().toString(), userId),
                createDummyExtraction(UUID.randomUUID().toString(), userId)
        );
        when(extractionInfoGateway.getByUserId(userId)).thenReturn(dummyList);

        // Act
        List<ExtractionInfo> resultList = extractionUseCase.getExtractionsByUserId(userId);

        // Assert
        assertNotNull(resultList);
        assertEquals(dummyList.size(), resultList.size());
        verify(extractionInfoGateway).getByUserId(userId);
    }

    @Test
    public void testStartExtractionWhenPending() {
        // Arrange
        String userId = "user123";
        String extractionId = UUID.randomUUID().toString();
        ExtractionInfo pendingExtraction = createDummyExtraction(extractionId, userId);
        // Ensure the dummy extraction is in PENDING status
        when(extractionInfoGateway.get(userId, extractionId)).thenReturn(pendingExtraction);
        when(extractionInfoGateway.save(any(ExtractionInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ExtractionInfo result = extractionUseCase.startExtraction(userId, extractionId);

        // Assert: status should change to IN_PROGRESS.
        assertNotNull(result);
        assertEquals(ExtractionStatus.IN_PROGRESS, result.status());
        verify(extractionInfoGateway).get(userId, extractionId);
        verify(extractionInfoGateway).save(any(ExtractionInfo.class));
    }

    @Test
    public void testStartExtractionWhenNotPending() {
        // Arrange
        String userId = "user123";
        String extractionId = UUID.randomUUID().toString();
        ExtractionInfo inProgressExtraction = createDummyExtraction(extractionId, userId);
        // Simulate a non-PENDING status (e.g., IN_PROGRESS)
        inProgressExtraction = new ExtractionInfo(
                inProgressExtraction.id(),
                inProgressExtraction.userId(),
                ExtractionStatus.IN_PROGRESS,
                inProgressExtraction.videoStoragePath(),
                inProgressExtraction.extractedFilePath(),
                inProgressExtraction.uploadAt(),
                inProgressExtraction.updatedAt(),
                inProgressExtraction.originalFile()
        );
        when(extractionInfoGateway.get(userId, extractionId)).thenReturn(inProgressExtraction);

        // Act & Assert: the call should throw an exception.
        ExtractionAlreadyBeingProcessedException exception = assertThrows(ExtractionAlreadyBeingProcessedException.class, () ->
                extractionUseCase.startExtraction(userId, extractionId)
        );
        assertEquals("extraction already being processed", exception.getMessage());
        verify(extractionInfoGateway).get(userId, extractionId);
        verify(extractionInfoGateway, never()).save(any());
    }

    @Test
    public void testUpdateStatus() {
        // Arrange
        String userId = "user123";
        String extractionId = UUID.randomUUID().toString();
        ExtractionInfo existingExtraction = createDummyExtraction(extractionId, userId);
        when(extractionInfoGateway.get(userId, extractionId)).thenReturn(existingExtraction);
        when(extractionInfoGateway.save(any(ExtractionInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act: update the status to COMPLETED.
        ExtractionInfo result = extractionUseCase.updateStatus(userId, extractionId, ExtractionStatus.COMPLETE);

        // Assert
        assertNotNull(result);
        assertEquals(ExtractionStatus.COMPLETE, result.status());
        verify(extractionInfoGateway).get(userId, extractionId);
        verify(extractionInfoGateway).save(any(ExtractionInfo.class));
    }

    // Utility method to create a dummy ExtractionInfo for testing purposes.
    private ExtractionInfo createDummyExtraction(String extractionId, String userId) {
        return new ExtractionInfo(
                extractionId,
                userId,
                ExtractionStatus.PENDING,
                "dummyUploadsPath",
                "dummyExtractedPath",
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                new ExtractionInfoFile("dummy.mp4", 12345L, "dummyhash")
        );
    }
}
