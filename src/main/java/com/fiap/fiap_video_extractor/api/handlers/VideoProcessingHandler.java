package com.fiap.fiap_video_extractor.api.handlers;

import com.fiap.fiap_video_extractor.core.usecases.VideoExtractionUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
public class VideoProcessingHandler {

    @Autowired
    private VideoExtractionUseCase videoExtractionUseCase;

    @PostMapping("/process-video")
    public ResponseEntity<String> processVideo(@RequestParam("file") MultipartFile file) {
        try {
            File zipFile = videoExtractionUseCase.processVideo(UUID.randomUUID().toString(), file);
            return ResponseEntity.ok().body("Frames extracted and saved to: " + zipFile.getAbsolutePath());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error processing video: " + e.getMessage());
        }
    }
}
