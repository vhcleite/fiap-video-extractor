package com.fiap.fiap_video_extractor.adapters.controllers;

import com.fiap.fiap_video_extractor.core.entities.ExtractionCommand;
import com.fiap.fiap_video_extractor.core.entities.ExtractionInfo;
import com.fiap.fiap_video_extractor.core.entities.ExtractionStatus;
import com.fiap.fiap_video_extractor.core.usecases.ExtractionUseCase;
import com.fiap.fiap_video_extractor.core.usecases.FileStorageUseCase;
import com.fiap.fiap_video_extractor.core.usecases.VideoProcessorUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@MockitoSettings
class ExtractionProcessorControllerTest {

    @Mock
    private ExtractionUseCase extractionUseCase;

    @Mock
    private FileStorageUseCase fileStorageUseCase;

    @Mock
    private VideoProcessorUseCase videoProcessorUseCase;

    @InjectMocks
    private ExtractionProcessorController extractionProcessorController;

    @Test
    void testExecuteExtraction_Success() throws Exception {
        // Arrange
        ExtractionCommand extractionCommand = new ExtractionCommand("extract456", "user123");
        ExtractionInfo mockExtractionInfo = mock(ExtractionInfo.class);
        InputStream mockVideoStream = new ByteArrayInputStream("video content".getBytes());
        Path mockZipFile = Files.createTempFile("test", ".zip");

        when(extractionUseCase.startExtraction("user123", "extract456")).thenReturn(mockExtractionInfo);
        when(mockExtractionInfo.videoStoragePath()).thenReturn("path/to/video.mp4");
        when(mockExtractionInfo.id()).thenReturn("extract456");
        when(mockExtractionInfo.userId()).thenReturn("user123");
        when(mockExtractionInfo.extractedFilePath()).thenReturn("path/to/extracted.zip");
        when(fileStorageUseCase.getFile("path/to/video.mp4")).thenReturn(mockVideoStream);
        when(videoProcessorUseCase.process("extract456", mockVideoStream)).thenReturn(mockZipFile);
        when(extractionUseCase.updateStatus("user123", "extract456", ExtractionStatus.COMPLETE))
                .thenReturn(mockExtractionInfo);

        // Act
        ExtractionInfo result = extractionProcessorController.executeExtraction(extractionCommand);

        // Assert
        assertNotNull(result);
        verify(fileStorageUseCase, times(1)).uploadFile("path/to/extracted.zip", mockZipFile);
        verify(extractionUseCase, times(1)).updateStatus("user123", "extract456", ExtractionStatus.COMPLETE);
        assertEquals(mockExtractionInfo, result);

        // Cleanup
        Files.deleteIfExists(mockZipFile);
    }

    @Test
    void testExecuteExtraction_Failure() throws Exception {
        // Arrange
        ExtractionCommand extractionCommand = new ExtractionCommand("extract456", "user123");
        ExtractionInfo mockExtractionInfo = mock(ExtractionInfo.class);
        InputStream mockVideoStream = new ByteArrayInputStream("video content".getBytes());

        when(extractionUseCase.startExtraction("user123", "extract456")).thenReturn(mockExtractionInfo);
        when(mockExtractionInfo.videoStoragePath()).thenReturn("path/to/video.mp4");
        when(mockExtractionInfo.id()).thenReturn("extract456");
        when(mockExtractionInfo.userId()).thenReturn("user123");
        when(fileStorageUseCase.getFile("path/to/video.mp4")).thenReturn(mockVideoStream);
        when(videoProcessorUseCase.process("extract456", mockVideoStream)).thenThrow(new RuntimeException("Processing error"));
        when(extractionUseCase.updateStatus("user123", "extract456", ExtractionStatus.ERROR)).thenReturn(mockExtractionInfo);

        // Act
        ExtractionInfo result = extractionProcessorController.executeExtraction(extractionCommand);

        // Assert
        assertNotNull(result);
        verify(extractionUseCase, times(1)).updateStatus("user123", "extract456", ExtractionStatus.ERROR);
        verify(fileStorageUseCase, never()).uploadFile(anyString(), any(Path.class));
    }
}
