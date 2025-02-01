package com.fiap.fiap_video_extractor.core.usecases;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.lang.String.format;

@Service
public class VideoExtractionUseCase {

    private final VideoProcessorUseCase videoProcessor;

    public VideoExtractionUseCase(VideoProcessorUseCase videoProcessor) {
        this.videoProcessor = videoProcessor;
    }

    public File processVideo(String extractionId, MultipartFile file) throws IOException {
        String framesFolderName = format("frames_%s", extractionId);
        String videoTempFileName = format("video_%s", extractionId);

        // Create a temporary directory to store frames
        Path outputFolder = Files.createTempDirectory(framesFolderName);
        File videoFile = Files.createTempFile(videoTempFileName, ".mp4").toFile();
        file.transferTo(videoFile);

        long interval = 20; // Interval in seconds
        videoProcessor.extractFrames(outputFolder, videoFile, interval);

        // Compress the frames into a ZIP file
        File zipFile = Files.createTempFile(framesFolderName, ".zip").toFile();
        zipExtractedImages(zipFile, outputFolder);

        Path destinationPath = moveFileToFinalFolder(zipFile);

        deleteTemporaryFiles(videoFile, outputFolder);

        return destinationPath.toFile(); // Return the moved file
    }

    private static void deleteTemporaryFiles(File videoFile, Path outputFolder) {
        videoFile.delete();
        for (File frameFile : outputFolder.toFile().listFiles()) {
            frameFile.delete();
        }
        outputFolder.toFile().delete();
    }

    private static Path moveFileToFinalFolder(File zipFile) throws IOException {
        String userHome = System.getProperty("user.home"); // Get the user's home directory
        Path destinationFolder = Paths.get(userHome, "Documents", "processed_videos"); // Save to ~/Documents/processed_videos
        Files.createDirectories(destinationFolder); // Create the folder if it doesn't exist

        Path destinationPath = destinationFolder.resolve(zipFile.getName()); // Define the destination path
        Files.move(zipFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING); // Move the file
        return destinationPath;
    }

    private static void zipExtractedImages(File zipFile, Path outputFolder) throws IOException {
        try (ZipOutputStream zipOut = new ZipOutputStream(new java.io.FileOutputStream(zipFile))) {
            for (File frameFile : Objects.requireNonNull(outputFolder.toFile().listFiles())) {
                ZipEntry entry = new ZipEntry(frameFile.getName());
                zipOut.putNextEntry(entry);
                Files.copy(frameFile.toPath(), zipOut);
                zipOut.closeEntry();
            }
        }
    }
}
