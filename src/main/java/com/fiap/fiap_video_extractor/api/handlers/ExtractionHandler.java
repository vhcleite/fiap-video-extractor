package com.fiap.fiap_video_extractor.api.handlers;

import com.fiap.fiap_video_extractor.adapters.controllers.ExtractionController;
import com.fiap.fiap_video_extractor.core.entities.ExtractionInfo;
import com.fiap.fiap_video_extractor.core.entities.ExtractionResult;
import com.fiap.fiap_video_extractor.core.requests.ExtractionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/fiap/extractions")
@Tag(name = "Video Extraction API", description = "Endpoints for managing video extractions")
public class ExtractionHandler {

    private final ExtractionController extractionController;

    public ExtractionHandler(ExtractionController extractionController) {
        this.extractionController = extractionController;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create new video extraction",
            description = "Upload a video file for processing and extraction",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Extraction created successfully",
                            content = @Content(schema = @Schema(implementation = ExtractionInfo.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input or missing file")
            })
    public ResponseEntity<ExtractionInfo> createExtraction(
            @Parameter(description = "User ID", required = true, in = ParameterIn.HEADER)
            @RequestHeader("x-user-id") String userId,

            @Parameter(description = "Video file to process", required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(type = "string", format = "binary")))
            @RequestParam("file") MultipartFile file,

            @Parameter(description = "Notification email address", required = true)
            @RequestParam("email") String email
    ) {
        var request = new ExtractionRequest(userId, email);
        var response = extractionController.createExtraction(request, file);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get extraction by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Extraction found",
                            content = @Content(schema = @Schema(implementation = ExtractionInfo.class))),
                    @ApiResponse(responseCode = "404", description = "Extraction not found")
            })
    public ResponseEntity<ExtractionInfo> getExtractionById(
            @Parameter(description = "User ID", required = true, in = ParameterIn.HEADER)
            @RequestHeader("x-user-id") String userId,

            @Parameter(description = "Extraction ID", required = true, in = ParameterIn.PATH)
            @PathVariable("id") String id
    ) {
        var response = extractionController.getExtractionInfo(userId, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get all extractions for user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of extractions",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ExtractionInfo.class))))
            })
    public ResponseEntity<List<ExtractionInfo>> getExtractionsByUserId(
            @Parameter(description = "User ID", required = true, in = ParameterIn.HEADER)
            @RequestHeader("x-user-id") String userId
    ) {
        var response = extractionController.getExtractionsByUserId(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Download extracted files",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ZIP file with extracted content",
                            content = @Content(mediaType = "application/octet-stream",
                                    schema = @Schema(type = "string", format = "binary"))),
                    @ApiResponse(responseCode = "404", description = "Extraction not found")
            })
    public ResponseEntity<InputStreamResource> downloadExtractionFileById(
            @Parameter(description = "User ID", required = true, in = ParameterIn.HEADER)
            @RequestHeader("x-user-id") String userId,

            @Parameter(description = "Extraction ID", required = true, in = ParameterIn.PATH)
            @PathVariable("id") String extractionId
    ) {
        ExtractionResult result = extractionController.getExtractionFile(userId, extractionId);

        InputStreamResource resource = new InputStreamResource(result.inputStream());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + result.fileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
