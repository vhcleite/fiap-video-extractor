package com.fiap.fiap_video_extractor.core.usecases;

import com.fiap.fiap_video_extractor.adapters.gateways.ExtractionCommandGateway;
import com.fiap.fiap_video_extractor.adapters.gateways.VideoProcessorGateway;
import com.fiap.fiap_video_extractor.core.entities.ExtractionInfo;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings
class VideoProcessorUseCaseTest {

    @Mock
    private ExtractionCommandGateway extractionCommandGateway;

    @Mock
    private VideoProcessorGateway videoProcessorGateway;

    @InjectMocks
    private VideoProcessorUseCase videoProcessorUseCase;

    @Test
    void testAsyncProcessVideo() {
        // Arrange
        ExtractionInfo extractionInfo = mock(ExtractionInfo.class);

        // Act
        videoProcessorUseCase.asyncProcessVideo(extractionInfo);

        // Assert
        verify(extractionCommandGateway, times(1)).sendExtractionCommand(extractionInfo);
    }

    @Test
    void testProcess() throws Exception {
        // Arrange
        String extractionId = "test123";
        String sampleVideoContent = "fake video content";
        InputStream videoInputStream = new ByteArrayInputStream(sampleVideoContent.getBytes(StandardCharsets.UTF_8));

        doAnswer(invocation -> {
            Path outputFolder = invocation.getArgument(0);
            // Simulate creating frame images inside outputFolder
            Path fakeFrame = outputFolder.resolve("frame1.jpg");
            Files.writeString(fakeFrame, "frame data");
            return null;
        }).when(videoProcessorGateway).extractFrames(any(Path.class), any(File.class), anyLong());

        // Act
        Path zipFile = videoProcessorUseCase.process(extractionId, videoInputStream);

        // Assert
        assertNotNull(zipFile);
        assertTrue(Files.exists(zipFile), "Zip file should exist");

        // Verify ZIP contents
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile))) {
            ZipEntry entry = zis.getNextEntry();
            assertNotNull(entry, "ZIP should contain at least one file");
            assertEquals("frame1.jpg", entry.getName(), "Frame file name should match");
        }

        // Verify cleanup
        verify(videoProcessorGateway, times(1)).extractFrames(any(Path.class), any(File.class), anyLong());
    }
}
