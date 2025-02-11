package com.fiap.fiap_video_extractor.adapters.gateways;

import com.fiap.fiap_video_extractor.core.entities.ExtractionCommand;
import com.fiap.fiap_video_extractor.core.entities.ExtractionInfo;
import com.fiap.fiap_video_extractor.core.entities.ExtractionInfoFile;
import com.fiap.fiap_video_extractor.core.entities.ExtractionStatus;
import com.fiap.fiap_video_extractor.pkg.interfaces.ExtractionCommandProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.time.OffsetDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@MockitoSettings
class ExtractionCommandGatewayTest {

    @Mock
    private ExtractionCommandProducer producer; // Mock the dependency

    @InjectMocks
    private ExtractionCommandGateway gateway; // Inject the mock into the class under test

    private ExtractionInfo extractionInfo;

    @BeforeEach
    void setUp() {
        // Initialize test data
        extractionInfo = new ExtractionInfo(
                "123",
                "user-456",
                ExtractionStatus.PENDING,
                "s3://video.mp4",
                "s3://frames.zip",
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                new ExtractionInfoFile("video.mp4", 1024L, "abc123")
        );
    }

    @Test
    void sendExtractionCommand_ShouldCallProducerWithCorrectCommand() {
        // Arrange
        ExtractionCommand expectedCommand = new ExtractionCommand(
                extractionInfo.id(),
                extractionInfo.userId()
        );

        // Act
        gateway.sendExtractionCommand(extractionInfo);

        // Assert
        verify(producer).sendExtractionInfo(expectedCommand); // Verify the producer was called with the correct command
        verifyNoMoreInteractions(producer); // Ensure no other interactions with the mock
    }
}