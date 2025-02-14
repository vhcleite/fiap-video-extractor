package com.fiap.fiap_video_extractor.external;

import com.fiap.fiap_video_extractor.pkg.dto.NotificationRequest;
import com.fiap.fiap_video_extractor.pkg.interfaces.NotificationProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class NotificationMailProducer implements NotificationProducer {
    Logger logger = LoggerFactory.getLogger(NotificationMailProducer.class);

    private final JavaMailSender mailSender;
    private final String mailFrom;

    public NotificationMailProducer(JavaMailSender mailSender, @Value("spring.mail.username") String mailFrom) {
        this.mailSender = mailSender;
        this.mailFrom = mailFrom;
    }

    @Override
    public void notifyExtractionComplete(NotificationRequest notificationRequest) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notificationRequest.email());
        message.setSubject("Extraction info");
        message.setText("error".equals(notificationRequest.type()) ? "your extraction resulted in error" : "you extraction resulted in success");
        message.setFrom(mailFrom);

        mailSender.send(message);
    }
}
