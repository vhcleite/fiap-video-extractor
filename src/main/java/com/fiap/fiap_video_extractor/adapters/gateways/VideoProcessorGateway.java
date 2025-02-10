package com.fiap.fiap_video_extractor.adapters.gateways;

import com.fiap.fiap_video_extractor.pkg.interfaces.VideoProcessor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Component
public class VideoProcessorGateway {

    private final VideoProcessor videoProcessor;

    public VideoProcessorGateway(VideoProcessor videoProcessor) {
        this.videoProcessor = videoProcessor;
    }

    public void extractFrames(Path outputFolder, File videoFile, Long interval) throws IOException {
        videoProcessor.extractFrames(outputFolder, videoFile, interval);
    }
}
