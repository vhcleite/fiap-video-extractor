package com.fiap.fiap_video_extractor.adapters.gateways;

import com.fiap.fiap_video_extractor.core.entities.ExtractionInfo;
import com.fiap.fiap_video_extractor.pkg.dto.NotificationRequest;
import com.fiap.fiap_video_extractor.pkg.interfaces.NotificationProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

class NotificationGatewayTest {

    private NotificationGateway notificationGateway;

    @Mock
    private NotificationProducer notificationProducer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        notificationGateway = new NotificationGateway(notificationProducer);
    }

    @Test
    void shouldNotifyExtractionComplete() {
        ExtractionInfo extractionInfo = new ExtractionInfo(
                "123", "user1", null, "video.mp4", "path", OffsetDateTime.now(), OffsetDateTime.now(), null
        );

        notificationGateway.notifyExtractionComplete(extractionInfo);

        ArgumentCaptor<NotificationRequest> captor = ArgumentCaptor.forClass(NotificationRequest.class);
        verify(notificationProducer).notifyExtractionComplete(captor.capture());
        NotificationRequest request = captor.getValue();

        assertEquals("user1", request.userId());
        assertEquals("123", request.extractionId());
        assertEquals("create", request.type());
    }

    @Test
    void shouldNotifyExtractionError() {
        ExtractionInfo extractionInfo = new ExtractionInfo(
                "456", "user2", null, "video.mp4", "path", OffsetDateTime.now(), OffsetDateTime.now(), null
        );

        notificationGateway.notifyExtractionError(extractionInfo);

        ArgumentCaptor<NotificationRequest> captor = ArgumentCaptor.forClass(NotificationRequest.class);
        verify(notificationProducer).notifyExtractionComplete(captor.capture());
        NotificationRequest request = captor.getValue();

        assertEquals("user2", request.userId());
        assertEquals("456", request.extractionId());
        assertEquals("error", request.type());
    }
}
