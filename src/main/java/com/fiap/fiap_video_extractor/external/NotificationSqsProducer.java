package com.fiap.fiap_video_extractor.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.fiap_video_extractor.pkg.dto.NotificationRequest;
import com.fiap.fiap_video_extractor.pkg.interfaces.NotificationProducer;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NotificationSqsProducer implements NotificationProducer {
    Logger logger = LoggerFactory.getLogger(NotificationSqsProducer.class);

    private final ObjectMapper mapper;
    private final String queueName;
    private final SqsTemplate sqsTemplate;

    public NotificationSqsProducer(
            ObjectMapper mapper,
            @Value("${config.aws.sqs.extraction-notification}") String queueName,
            SqsTemplate sqsTemplate) {
        this.mapper = mapper;
        this.queueName = queueName;
        this.sqsTemplate = sqsTemplate;
    }

    @Override
    public void notifyExtractionComplete(NotificationRequest notificationRequest) {
        String messageBody = serializeExtractionInfo(notificationRequest);
        var result = sqsTemplate.send(sqsSendOptions -> sqsSendOptions.queue(queueName).payload(messageBody));
        logger.info("Message sent to SQS: {}", result.endpoint());
    }

    private String serializeExtractionInfo(NotificationRequest notificationRequest) {
        try {
            return mapper.writeValueAsString(notificationRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing ExtractionInfo", e);
        }
    }
}
