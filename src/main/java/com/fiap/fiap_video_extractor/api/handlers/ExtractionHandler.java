package com.fiap.fiap_video_extractor.api.handlers;

import com.fiap.fiap_video_extractor.adapters.controllers.ExtractionController;
import com.fiap.fiap_video_extractor.core.entities.ExtractionInfo;
import com.fiap.fiap_video_extractor.core.entities.ExtractionResult;
import com.fiap.fiap_video_extractor.core.requests.ExtractionRequest;
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
public class ExtractionHandler {

    private final ExtractionController extractionController;

    public ExtractionHandler(ExtractionController extractionController) {
        this.extractionController = extractionController;
    }

    @PostMapping
    public ResponseEntity<ExtractionInfo> createExtraction(
            @RequestHeader("x-user-id") String userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("email") String email
    ) {
        var request = new ExtractionRequest(userId, email);
        var response = extractionController.createExtraction(request, file);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExtractionInfo> getExtractionById(
            @RequestHeader("x-user-id") String userId,
            @PathVariable("id") String id
    ) {
        var response = extractionController.getExtractionInfo(userId, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ExtractionInfo>> getExtractionsByUserId(
            @RequestHeader("x-user-id") String userId
    ) {
        var response = extractionController.getExtractionsByUserId(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> downloadExtractionFileById(
            @RequestHeader("x-user-id") String userId,
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
