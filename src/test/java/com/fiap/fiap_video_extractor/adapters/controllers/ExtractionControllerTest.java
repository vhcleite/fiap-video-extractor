package com.fiap.fiap_video_extractor.adapters.controllers;

import com.fiap.fiap_video_extractor.core.entities.ExtractionInfo;
import com.fiap.fiap_video_extractor.core.entities.ExtractionResult;
import com.fiap.fiap_video_extractor.core.entities.ExtractionStatus;
import com.fiap.fiap_video_extractor.core.requests.ExtractionRequest;
import com.fiap.fiap_video_extractor.core.usecases.ExtractionUseCase;
import com.fiap.fiap_video_extractor.core.usecases.FileStorageUseCase;
import com.fiap.fiap_video_extractor.core.usecases.VideoProcessorUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings
class ExtractionControllerTest {

    @Mock
    private ExtractionUseCase extractionUseCase;

    @Mock
    private FileStorageUseCase fileStorageUseCase;

    @Mock
    private VideoProcessorUseCase videoProcessorUseCase;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private ExtractionController extractionController;

    @Test
    void testCreateExtraction() {
        // Arrange
        ExtractionRequest request = mock(ExtractionRequest.class);
        ExtractionInfo mockExtractionInfo = mock(ExtractionInfo.class);

        when(extractionUseCase.createExtraction(any(), any())).thenReturn(mockExtractionInfo);
        when(mockExtractionInfo.videoStoragePath()).thenReturn("path/to/video.mp4");

        // Act
        ExtractionInfo result = extractionController.createExtraction(request, multipartFile);

        // Assert
        assertNotNull(result);
        verify(extractionUseCase, times(1)).createExtraction(request, multipartFile);
        verify(fileStorageUseCase, times(1)).uploadFile("path/to/video.mp4", multipartFile);
        verify(videoProcessorUseCase, times(1)).asyncProcessVideo(mockExtractionInfo);
    }

    @Test
    void testGetExtractionInfo() {
        // Arrange
        String userId = "user123";
        String extractionId = "extract456";
        ExtractionInfo mockExtractionInfo = mock(ExtractionInfo.class);

        when(extractionUseCase.getExtractionInfo(userId, extractionId)).thenReturn(mockExtractionInfo);

        // Act
        ExtractionInfo result = extractionController.getExtractionInfo(userId, extractionId);

        // Assert
        assertNotNull(result);
        verify(extractionUseCase, times(1)).getExtractionInfo(userId, extractionId);
    }

    @Test
    void testGetExtractionsByUserId() {
        // Arrange
        String userId = "user123";
        List<ExtractionInfo> mockExtractionList = List.of(mock(ExtractionInfo.class), mock(ExtractionInfo.class));

        when(extractionUseCase.getExtractionsByUserId(userId)).thenReturn(mockExtractionList);

        // Act
        List<ExtractionInfo> result = extractionController.getExtractionsByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(extractionUseCase, times(1)).getExtractionsByUserId(userId);
    }

    @Test
    void testGetExtractionFile_Success() {
        // Arrange
        String userId = "user123";
        String extractionId = "extract456";
        ExtractionInfo mockExtractionInfo = mock(ExtractionInfo.class);
        InputStream mockInputStream = new ByteArrayInputStream("file content".getBytes());

        when(mockExtractionInfo.status()).thenReturn(ExtractionStatus.COMPLETE);
        when(mockExtractionInfo.extractedFilePath()).thenReturn("path/to/extracted.zip");
        when(extractionUseCase.getExtractionInfo(userId, extractionId)).thenReturn(mockExtractionInfo);
        when(fileStorageUseCase.getFile("path/to/extracted.zip")).thenReturn(mockInputStream);

        // Act
        ExtractionResult result = extractionController.getExtractionFile(userId, extractionId);

        // Assert
        assertNotNull(result);
        assertEquals("path/to/extracted.zip", result.fileName());
        verify(extractionUseCase, times(1)).getExtractionInfo(userId, extractionId);
        verify(fileStorageUseCase, times(1)).getFile("path/to/extracted.zip");
    }

    @Test
    void testGetExtractionFile_Failure_NotReady() {
        // Arrange
        String userId = "user123";
        String extractionId = "extract456";
        ExtractionInfo mockExtractionInfo = mock(ExtractionInfo.class);

        when(mockExtractionInfo.status()).thenReturn(ExtractionStatus.IN_PROGRESS);
        when(extractionUseCase.getExtractionInfo(userId, extractionId)).thenReturn(mockExtractionInfo);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> extractionController.getExtractionFile(userId, extractionId));

        assertEquals("extraction not ready to download", exception.getMessage());
        verify(extractionUseCase, times(1)).getExtractionInfo(userId, extractionId);
        verify(fileStorageUseCase, never()).getFile(any());
    }
}
