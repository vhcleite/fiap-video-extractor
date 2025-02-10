package com.fiap.fiap_video_extractor.adapters.gateways;

import com.fiap.fiap_video_extractor.pkg.interfaces.FileStorageDatasource;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.InputStream;
import java.nio.file.Path;

import static org.mockito.Mockito.*;

@MockitoSettings
class FileStorageGatewayTest {

    @InjectMocks
    private FileStorageGateway fileStorageGateway;

    @Mock
    private FileStorageDatasource fileStorageDatasource;

    @Mock
    private Path file;

    @Mock
    private InputStream inputStream;

    @Test
    void testUploadFile() {
        String objectKey = "test-object-key";

        // Call the method under test
        fileStorageGateway.uploadFile(objectKey, file);

        // Verify that the uploadFile method on the fileStorageDatasource was called with correct arguments
        verify(fileStorageDatasource, times(1)).uploadFile(objectKey, file);
    }

    @Test
    void testGetFile() {
        String objectKey = "test-object-key";
        when(fileStorageDatasource.getFile(objectKey)).thenReturn(inputStream);

        // Call the method under test
        InputStream result = fileStorageGateway.getFile(objectKey);

        // Verify that the getFile method was called on fileStorageDatasource
        verify(fileStorageDatasource, times(1)).getFile(objectKey);

        // Verify that the result is the mock InputStream
        assert result == inputStream;
    }
}
