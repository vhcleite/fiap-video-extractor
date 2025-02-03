package com.fiap.fiap_video_extractor.api.handlers;

import com.fiap.fiap_video_extractor.core.entities.ExtractionInfo;
import com.fiap.fiap_video_extractor.core.entities.ExtractionResult;
import com.fiap.fiap_video_extractor.core.requests.ExtractionRequest;
import com.fiap.fiap_video_extractor.core.usecases.ExtractionUseCase;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/fiap/extractions")
public class ExtractionHandler {

    private final ExtractionUseCase extractionUseCase;

    public ExtractionHandler(ExtractionUseCase extractionUseCase) {
        this.extractionUseCase = extractionUseCase;
    }

    @PostMapping
    public ResponseEntity<ExtractionInfo> getExtractionById(
            @RequestHeader("x-user-id") String userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("email") String email
    ) {
        var request = new ExtractionRequest(userId, email);
        var response = extractionUseCase.extractFile(request, file);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExtractionInfo> getExtractionById(
            @RequestHeader("x-user-id") String userId,
            @PathVariable("id") String id
    ) {
        var response = extractionUseCase.getExtractionInfo(userId, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> downloadExtractionFileById(
            @RequestHeader("x-user-id") String userId,
            @PathVariable("id") String extractionId
    ) {
        ExtractionResult result = extractionUseCase.getExtractionFile(userId, extractionId);

        InputStreamResource resource = new InputStreamResource(result.inputStream());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + result.fileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
