package com.fiap.fiap_video_extractor.core.usecases;

import com.fiap.fiap_video_extractor.adapters.gateways.FileStorageGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@MockitoSettings
public class FileStorageUseCaseTest {

    @Mock
    private FileStorageGateway fileStorageGateway;

    @InjectMocks
    private FileStorageUseCase fileStorageUseCase;

    @Test
    public void testGetFile() throws Exception {
        // Arrange
        String filePath = "some/path/to/file.txt";
        String fileContent = "Hello, World!";
        InputStream expectedStream = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8));
        when(fileStorageGateway.getFile(filePath)).thenReturn(expectedStream);

        // Act
        InputStream resultStream = fileStorageUseCase.getFile(filePath);

        // Assert
        assertNotNull(resultStream);
        byte[] resultBytes = resultStream.readAllBytes();
        String resultContent = new String(resultBytes, StandardCharsets.UTF_8);
        assertEquals(fileContent, resultContent);
        verify(fileStorageGateway, times(1)).getFile(filePath);
    }

    @Test
    public void testUploadFileWithPath(@TempDir Path tempDir) throws Exception {
        // Arrange
        String filePath = "destination/path/file.txt";
        // Create a temporary file in the provided temp directory to simulate an existing file.
        Path fileToUpload = Files.createTempFile(tempDir, "testFile", ".txt");
        String fileContent = "Content to upload";
        Files.writeString(fileToUpload, fileContent, StandardCharsets.UTF_8);

        // Act
        fileStorageUseCase.uploadFile(filePath, fileToUpload);

        // Assert
        verify(fileStorageGateway, times(1)).uploadFile(filePath, fileToUpload);
    }

    @Test
    public void testUploadFileWithMultipartFile() {
        // Arrange
        String objectKey = "object/key/file.txt";
        String fileContent = "Multipart file content";
        byte[] contentBytes = fileContent.getBytes(StandardCharsets.UTF_8);
        String originalFilename = "file.txt";
        MultipartFile multipartFile = new MockMultipartFile("file", originalFilename, "text/plain", contentBytes);

        // We want to capture the file passed to fileStorageGateway.uploadFile. Since the method
        // deletes the temporary file after uploading, we use a Mockito answer to read its content
        // before deletion and capture its path.
        final byte[][] capturedContent = new byte[1][];
        final Path[] capturedPath = new Path[1];

        doAnswer(invocation -> {
            capturedPath[0] = invocation.getArgument(1);
            // Read the file's content before it is deleted.
            capturedContent[0] = Files.readAllBytes(capturedPath[0]);
            return null;
        }).when(fileStorageGateway).uploadFile(eq(objectKey), any(Path.class));

        // Act
        fileStorageUseCase.uploadFile(objectKey, multipartFile);

        // Assert
        // Verify that the file content that was copied matches the original content.
        assertNotNull(capturedContent[0], "The file content should have been captured.");
        String uploadedContent = new String(capturedContent[0], StandardCharsets.UTF_8);
        assertEquals(fileContent, uploadedContent);

        // Verify that the temporary file has been deleted after the upload.
        assertNotNull(capturedPath[0], "A temporary file path should have been captured.");
        assertFalse(Files.exists(capturedPath[0]), "The temporary file should have been deleted.");

        verify(fileStorageGateway, times(1)).uploadFile(eq(objectKey), any(Path.class));
    }
}
