package com.fiap.fiap_video_extractor.api.handlers;

import com.fiap.fiap_video_extractor.adapters.controllers.ExtractionProcessorController;
import com.fiap.fiap_video_extractor.core.entities.ExtractionCommand;
import com.fiap.fiap_video_extractor.core.exceptions.ExtractionAlreadyBeingProcessedException;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExtractionProcessorListener {

    private static final Logger log = LoggerFactory.getLogger(ExtractionProcessorListener.class);

    private final ExtractionProcessorController processor;

    public ExtractionProcessorListener(ExtractionProcessorController processor) {
        this.processor = processor;
    }

    @SqsListener("${events.queues.extraction-info}")
    public void receiveStringMessage(ExtractionCommand extractionCommand) {
        try {
            var extraction = processor.executeExtraction(extractionCommand);
            log.info("extraction {} complete", extraction.id());
        } catch (
                ExtractionAlreadyBeingProcessedException ex) {
            log.info("ignoring extraction as it already started");
        }
    }
}
