package com.fiap.fiap_video_extractor.api.handlers;

import com.fiap.fiap_video_extractor.adapters.controllers.ExtractionController;
import com.fiap.fiap_video_extractor.core.entities.ExtractionInfo;
import com.fiap.fiap_video_extractor.core.entities.ExtractionInfoFile;
import com.fiap.fiap_video_extractor.core.entities.ExtractionResult;
import com.fiap.fiap_video_extractor.core.entities.ExtractionStatus;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExtractionHandlerTest {

    @Mock
    private ExtractionController extractionController;

    @InjectMocks
    private ExtractionHandler extractionHandler;

    private final ExtractionInfo mockExtraction = new ExtractionInfo(
            "123", "user-456", ExtractionStatus.PENDING,
            "s3://video.mp4", "s3://frames.zip",
            OffsetDateTime.now(), OffsetDateTime.now(),
            new ExtractionInfoFile("video.mp4", 1024L, "abc123")
    );

    @BeforeEach
    void setup() {
        // Configure MockMVC with the handler instance
        RestAssuredMockMvc.standaloneSetup(extractionHandler);
    }

    @Test
    void createExtraction_ShouldReturnCreated() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.mp4", "video/mp4", "test content".getBytes()
        );

        when(extractionController.createExtraction(any(), any()))
                .thenReturn(mockExtraction);

        given()
                .multiPart("file", file.getOriginalFilename(), file.getBytes())
                .param("email", "test@example.com")
                .header("x-user-id", "user-456")
                .when()
                .post("/fiap/extractions")
                .then()
                .status(HttpStatus.CREATED)
                .body("id", equalTo("123"))
                .body("userId", equalTo("user-456"));
    }

    @Test
    void getExtractionById_ShouldReturnOk() {
        Mockito.when(extractionController.getExtractionInfo(anyString(), anyString()))
                .thenReturn(mockExtraction);

        given()
                .header("x-user-id", "user-456")
                .when()
                .get("/fiap/extractions/123")
                .then()
                .status(HttpStatus.OK)
                .body("id", equalTo("123"))
                .body("status", equalTo("PENDING"));
    }

    @Test
    void getExtractionsByUserId_ShouldReturnList() {
        Mockito.when(extractionController.getExtractionsByUserId(anyString()))
                .thenReturn(List.of(mockExtraction));

        given()
                .header("x-user-id", "user-456")
                .when()
                .get("/fiap/extractions")
                .then()
                .status(HttpStatus.OK)
                .body("$", hasSize(1))
                .body("[0].userId", equalTo("user-456"));
    }

    @Test
    void downloadExtractionFile_ShouldReturnFile() throws IOException {
        byte[] fileContent = "test content".getBytes();
        ExtractionResult mockResult = new ExtractionResult(
                "result.zip",
                (new ByteArrayResource(fileContent)).getInputStream()
        );

        Mockito.when(extractionController.getExtractionFile(anyString(), anyString()))
                .thenReturn(mockResult);

        given()
                .header("x-user-id", "user-456")
                .when()
                .get("/fiap/extractions/123/download")
                .then()
                .status(HttpStatus.OK)
                .header("Content-Disposition", containsString("result.zip"))
                .contentType("application/octet-stream")
                .body(equalTo("test content"));
    }

    @Test
    void createExtraction_ShouldHandleMissingFile() {
        given()
                .contentType("multipart/form-data") // Force multipart request
                .multiPart("email", "test@example.com") // Add email as form field
                .header("x-user-id", "user-456")
                .when()
                .post("/fiap/extractions")
                .then()
                .status(HttpStatus.BAD_REQUEST);
    }
}