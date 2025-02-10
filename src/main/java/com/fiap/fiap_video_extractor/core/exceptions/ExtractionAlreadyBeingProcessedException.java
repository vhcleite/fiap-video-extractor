package com.fiap.fiap_video_extractor.core.exceptions;

public class ExtractionAlreadyBeingProcessedException extends RuntimeException {
    public ExtractionAlreadyBeingProcessedException(String message) {
        super(message);
    }
}
