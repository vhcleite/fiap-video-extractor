package com.fiap.fiap_video_extractor.api.handlers;

import com.fiap.fiap_video_extractor.adapters.controllers.ExtractionProcessorController;
import com.fiap.fiap_video_extractor.core.entities.ExtractionCommand;
import com.fiap.fiap_video_extractor.core.entities.ExtractionInfo;
import com.fiap.fiap_video_extractor.core.entities.ExtractionInfoFile;
import com.fiap.fiap_video_extractor.core.entities.ExtractionStatus;
import com.fiap.fiap_video_extractor.core.exceptions.ExtractionAlreadyBeingProcessedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.time.OffsetDateTime;

import static org.mockito.Mockito.*;

@MockitoSettings
class ExtractionProcessorListenerTest {

    @Mock
    private ExtractionProcessorController processor;

    @InjectMocks
    private ExtractionProcessorListener listener;


    private ExtractionCommand mockCommand;
    private ExtractionInfo mockExtractionInfo;

    @BeforeEach
    void setUp() {
        // Creating mock ExtractionCommand
        mockCommand = new ExtractionCommand("123", "user123");

        // Creating mock ExtractionInfo response
        ExtractionInfoFile originalFile = new ExtractionInfoFile("video.mp4", 1024L, "fileHash");
        mockExtractionInfo = new ExtractionInfo(
                "extraction123",
                "user123",
                ExtractionStatus.COMPLETE,
                "s3://bucket/video.mp4",
                "s3://bucket/extracted.zip",
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                originalFile
        );
    }

    @Test
    void shouldProcessExtractionSuccessfully() {
        when(processor.executeExtraction(mockCommand)).thenReturn(mockExtractionInfo);

        listener.receiveStringMessage(mockCommand);

        verify(processor, times(1)).executeExtraction(mockCommand);
    }

    @Test
    void shouldIgnoreAlreadyProcessingExtraction() {
        doThrow(new ExtractionAlreadyBeingProcessedException("Extraction already in progress"))
                .when(processor).executeExtraction(mockCommand);

        listener.receiveStringMessage(mockCommand);

        verify(processor, times(1)).executeExtraction(mockCommand);
    }
}
