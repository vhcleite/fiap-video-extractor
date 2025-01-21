package com.fiap.fiap_video_extractor.external;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.fiap_video_extractor.core.entities.ExtractionInfo;
import com.fiap.fiap_video_extractor.pkg.interfaces.ExtractionCommandProducer;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ExtractionCommandSqsProducer implements ExtractionCommandProducer {

    Logger logger = LoggerFactory.getLogger(ExtractionCommandSqsProducer.class);

    private final ObjectMapper mapper;
    private final String queueName;
    private final SqsTemplate sqsTemplate;

    public ExtractionCommandSqsProducer(
            ObjectMapper mapper,
            @Value("${events.queues.extraction-info}") String queueName, SqsTemplate sqsTemplate
    ) {
        this.mapper = mapper;
        this.queueName = queueName;
        this.sqsTemplate = sqsTemplate;
    }

    public void sendExtractionInfo(ExtractionInfo extractionInfo) {
        String messageBody = serializeExtractionInfo(extractionInfo);
        var result = sqsTemplate.send(sqsSendOptions -> sqsSendOptions.queue(queueName).payload(messageBody));
        logger.info("Message sent to SQS: {}", result.endpoint());
    }

    private String serializeExtractionInfo(ExtractionInfo extractionInfo) {
        try {
            return mapper.writeValueAsString(extractionInfo);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing ExtractionInfo", e);
        }
    }
}
