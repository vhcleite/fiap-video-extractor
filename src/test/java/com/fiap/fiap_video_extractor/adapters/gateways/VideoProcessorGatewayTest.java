package com.fiap.fiap_video_extractor.adapters.gateways;

import com.fiap.fiap_video_extractor.pkg.interfaces.VideoProcessor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@MockitoSettings
class VideoProcessorGatewayTest {

    @InjectMocks
    private VideoProcessorGateway videoProcessorGateway;

    @Mock
    private VideoProcessor videoProcessor;

    @Mock
    private Path outputFolder;

    @Mock
    private File videoFile;

    @Test
    void testExtractFrames() throws IOException {
        long interval = 20L;

        // Call the method under test
        videoProcessorGateway.extractFrames(outputFolder, videoFile, interval);

        // Verify that the extractFrames method was called on the videoProcessor with the correct arguments
        verify(videoProcessor, times(1)).extractFrames(outputFolder, videoFile, interval);
    }
}
