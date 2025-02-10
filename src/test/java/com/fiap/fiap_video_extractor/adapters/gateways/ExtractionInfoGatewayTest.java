package com.fiap.fiap_video_extractor.adapters.gateways;

import com.fiap.fiap_video_extractor.core.entities.ExtractionInfo;
import com.fiap.fiap_video_extractor.core.entities.ExtractionInfoFile;
import com.fiap.fiap_video_extractor.core.entities.ExtractionStatus;
import com.fiap.fiap_video_extractor.pkg.interfaces.ExtractionInfoDatasource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings
class ExtractionInfoGatewayTest {

    @Mock
    private ExtractionInfoDatasource extractionInfoDatasource; // Mock the dependency

    @InjectMocks
    private ExtractionInfoGateway gateway; // Inject the mock into the class under test

    private ExtractionInfo extractionInfo;

    @BeforeEach
    void setUp() {
        // Initialize test data
        extractionInfo = new ExtractionInfo(
                "123", // id
                "user-456", // userId
                ExtractionStatus.PENDING, // status
                "s3://video.mp4", // videoStoragePath
                "s3://frames.zip", // extractedFilePath
                OffsetDateTime.now(), // uploadAt
                OffsetDateTime.now(), // updatedAt
                new ExtractionInfoFile("video.mp4", 1024L, "abc123") // originalFile
        );
    }

    @Test
    void save_ShouldDelegateToDatasource() {
        // Arrange
        when(extractionInfoDatasource.save(extractionInfo)).thenReturn(extractionInfo);

        // Act
        ExtractionInfo result = gateway.save(extractionInfo);

        // Assert
        verify(extractionInfoDatasource).save(extractionInfo); // Verify the datasource was called
        assertEquals(extractionInfo, result); // Verify the result is as expected
    }

    @Test
    void get_ShouldDelegateToDatasource() {
        // Arrange
        String userId = "user-456";
        String id = "123";
        when(extractionInfoDatasource.get(userId, id)).thenReturn(extractionInfo);

        // Act
        ExtractionInfo result = gateway.get(userId, id);

        // Assert
        verify(extractionInfoDatasource).get(userId, id); // Verify the datasource was called
        assertEquals(extractionInfo, result); // Verify the result is as expected
    }

    @Test
    void getByUserId_ShouldDelegateToDatasource() {
        // Arrange
        String userId = "user-456";
        List<ExtractionInfo> expectedList = Collections.singletonList(extractionInfo);
        when(extractionInfoDatasource.getByUserId(userId)).thenReturn(expectedList);

        // Act
        List<ExtractionInfo> result = gateway.getByUserId(userId);

        // Assert
        verify(extractionInfoDatasource).getByUserId(userId); // Verify the datasource was called
        assertEquals(expectedList, result); // Verify the result is as expected
    }
}