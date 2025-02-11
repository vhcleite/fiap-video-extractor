package com.fiap.fiap_video_extractor.core.usecases;

import com.fiap.fiap_video_extractor.adapters.gateways.NotificationGateway;
import com.fiap.fiap_video_extractor.core.entities.ExtractionInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class NotificationUseCaseTest {

    private NotificationGateway notificationGateway;
    private NotificationUseCase notificationUseCase;

    @BeforeEach
    void setUp() {
        notificationGateway = mock(NotificationGateway.class);
        notificationUseCase = new NotificationUseCase(notificationGateway);
    }

    @Test
    void shouldNotifyExtractionComplete() {
        ExtractionInfo extractionInfo = mock(ExtractionInfo.class);

        notificationUseCase.notifyExtractionComplete(extractionInfo);

        verify(notificationGateway, times(1)).notifyExtractionComplete(extractionInfo);
    }

    @Test
    void shouldNotifyExtractionError() {
        ExtractionInfo extractionInfo = mock(ExtractionInfo.class);

        notificationUseCase.notifyExtractionError(extractionInfo);

        verify(notificationGateway, times(1)).notifyExtractionError(extractionInfo);
    }
}
