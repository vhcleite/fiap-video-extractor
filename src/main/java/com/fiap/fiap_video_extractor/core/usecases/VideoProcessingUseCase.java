package com.fiap.fiap_video_extractor.core.usecases;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipOutputStream;

@Service
public class VideoProcessingUseCase {

    private static final String FFMPEG_PATH = "ffmpeg"; // Ensure FFmpeg is installed and in PATH
    private static final String FFPROBE_PATH = "ffprobe"; // Ensure FFprobe is installed and in PATH


    public File processVideo(MultipartFile file) throws IOException {
        // Create a temporary directory to store frames
        Path outputFolder = Files.createTempDirectory("frames");
        File videoFile = Files.createTempFile("video", ".mp4").toFile();
        file.transferTo(videoFile);

        // Analyze the video
        FFprobe ffprobe = new FFprobe(FFPROBE_PATH);
        FFmpegProbeResult probeResult = ffprobe.probe(videoFile.getAbsolutePath());
        Duration duration = Duration.ofSeconds(Double.valueOf(probeResult.getFormat().duration).longValue());

        // Extract frames at 20-second intervals
        FFmpeg ffmpeg = new FFmpeg(FFMPEG_PATH);
        long interval = 20; // Interval in seconds
        for (long currentTime = 0; currentTime < duration.getSeconds(); currentTime += interval) {
            String outputPath = outputFolder.resolve("frame_at_" + currentTime + ".jpg").toString();
            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(videoFile.getAbsolutePath())
                    .overrideOutputFiles(true)
                    .addOutput(outputPath)
                    .setFrames(1)
                    .setStartOffset(currentTime, TimeUnit.SECONDS)
                    .done();
            new FFmpegExecutor(ffmpeg).createJob(builder).run();
        }

        // Compress the frames into a ZIP file
        File zipFile = Files.createTempFile("frames", ".zip").toFile();
        try (ZipOutputStream zipOut = new ZipOutputStream(new java.io.FileOutputStream(zipFile))) {
            for (File frameFile : outputFolder.toFile().listFiles()) {
                java.util.zip.ZipEntry entry = new java.util.zip.ZipEntry(frameFile.getName());
                zipOut.putNextEntry(entry);
                Files.copy(frameFile.toPath(), zipOut);
                zipOut.closeEntry();
            }
        }

        // Move the ZIP file to the user's folder
        String userHome = System.getProperty("user.home"); // Get the user's home directory
        Path destinationFolder = Paths.get(userHome, "Documents", "processed_videos"); // Save to ~/Documents/processed_videos
        Files.createDirectories(destinationFolder); // Create the folder if it doesn't exist

        Path destinationPath = destinationFolder.resolve(zipFile.getName()); // Define the destination path
        Files.move(zipFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING); // Move the file

        // Clean up temporary files
        videoFile.delete();
        for (File frameFile : outputFolder.toFile().listFiles()) {
            frameFile.delete();
        }
        outputFolder.toFile().delete();

        return destinationPath.toFile(); // Return the moved file
    }
}
