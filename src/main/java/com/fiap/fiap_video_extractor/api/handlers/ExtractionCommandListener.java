package com.fiap.fiap_video_extractor.api.handlers;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExtractionCommandListener {

    Logger logger = LoggerFactory.getLogger(ExtractionCommandListener.class);

    @SqsListener("${events.queues.extraction-info}")
    public void receiveStringMessage(String message) {
        logger.info("Received message: {}", message);
    }
}
