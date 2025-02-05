package com.fiap.fiap_video_extractor.pkg.interfaces;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public interface VideoProcessor {
    void extractFrames(Path outputFolder, File videoFile, Long interval) throws IOException;
}
