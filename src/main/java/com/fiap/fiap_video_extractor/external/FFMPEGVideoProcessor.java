package com.fiap.fiap_video_extractor.external;

import com.fiap.fiap_video_extractor.pkg.interfaces.VideoProcessor;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class FFMPEGVideoProcessor implements VideoProcessor {
    private static final String FFMPEG_PATH = "ffmpeg"; // Ensure FFmpeg is installed and in PATH
    private static final String FFPROBE_PATH = "ffprobe"; // Ensure FFprobe is installed and in PATH

    public void extractFrames(Path outputFolder, File videoFile, Long interval) throws IOException {
        FFprobe ffprobe = new FFprobe(FFPROBE_PATH);
        FFmpegProbeResult probeResult = ffprobe.probe(videoFile.getAbsolutePath());
        Duration duration = Duration.ofSeconds(Double.valueOf(probeResult.getFormat().duration).longValue());

        FFmpeg ffmpeg = new FFmpeg(FFMPEG_PATH);
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
    }
}
