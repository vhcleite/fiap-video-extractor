package com.fiap.fiap_video_extractor.api.handlers;

import com.fiap.fiap_video_extractor.core.entities.ExtractionCommand;
import com.fiap.fiap_video_extractor.core.usecases.ExtractionUseCase;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExtractionCommandListener {

    private final ExtractionUseCase extractionUseCase;

    Logger logger = LoggerFactory.getLogger(ExtractionCommandListener.class);

    public ExtractionCommandListener(ExtractionUseCase extractionUseCase) {
        this.extractionUseCase = extractionUseCase;
    }

    @SqsListener("${events.queues.extraction-info}")
    public void receiveStringMessage(ExtractionCommand extractionCommand) {
        extractionUseCase.executeExtraction(extractionCommand);
    }
}
