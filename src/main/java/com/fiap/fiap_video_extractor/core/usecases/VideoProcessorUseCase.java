package com.fiap.fiap_video_extractor.core.usecases;

import com.fiap.fiap_video_extractor.adapters.gateways.ExtractionCommandGateway;
import com.fiap.fiap_video_extractor.adapters.gateways.VideoProcessorGateway;
import com.fiap.fiap_video_extractor.core.entities.ExtractionInfo;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.lang.String.format;

@Service
public class VideoProcessorUseCase {

    private final ExtractionCommandGateway extractionCommandGateway;
    private final VideoProcessorGateway videoProcessorGateway;

    public VideoProcessorUseCase(
            ExtractionCommandGateway extractionCommandGateway,
            VideoProcessorGateway videoProcessorGateway) {
        this.extractionCommandGateway = extractionCommandGateway;
        this.videoProcessorGateway = videoProcessorGateway;
    }

    public void asyncProcessVideo(ExtractionInfo extractionInfo) {
        extractionCommandGateway.sendExtractionCommand(extractionInfo);
    }

    public Path process(String extractionId, InputStream videoInputStream) throws IOException {
        String framesFolderName = format("frames_%s", extractionId);
        String videoTempFileName = format("video_%s", extractionId);

        Path outputFolder = Files.createTempDirectory(framesFolderName);
        File videoFile = Files.createTempFile(videoTempFileName, ".mp4").toFile();

        try (videoInputStream) {
            Files.copy(videoInputStream, videoFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            long intervalInSeconds = 20;
            videoProcessorGateway.extractFrames(outputFolder, videoFile, intervalInSeconds);

            // Compress the frames into a ZIP file
            Path zipFile = Files.createTempFile(framesFolderName, ".zip");
            zipExtractedImages(zipFile, outputFolder);
            return zipFile;
        } finally {
            deleteTemporaryFiles(videoFile, outputFolder);
        }
    }

    private static void deleteTemporaryFiles(File videoFile, Path outputFolder) {
        videoFile.delete();
        for (File frameFile : outputFolder.toFile().listFiles()) {
            frameFile.delete();
        }
        outputFolder.toFile().delete();
    }

    private static void zipExtractedImages(Path zipFile, Path outputFolder) throws IOException {
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile.toFile()))) {
            for (File frameFile : Objects.requireNonNull(outputFolder.toFile().listFiles())) {
                ZipEntry entry = new ZipEntry(frameFile.getName());
                zipOut.putNextEntry(entry);
                Files.copy(frameFile.toPath(), zipOut);
                zipOut.closeEntry();
            }
        }
    }
}
