package com.fiap.fiap_video_extractor.api.handlers;

import com.fiap.fiap_video_extractor.core.entities.ExtractionInfo;
import com.fiap.fiap_video_extractor.core.requests.ExtractionRequest;
import com.fiap.fiap_video_extractor.core.usecases.ExtractionUseCase;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ExtractionInfo> create(
            @RequestHeader("x-user-id") String userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("email") String email
    ) {
        System.out.println("userId: " + userId);
        System.out.println("email: " + email);
        System.out.println("file name: " + file.getOriginalFilename());
        System.out.println("file size: " + file.getSize());
        var request = new ExtractionRequest(userId, email);
        var response = extractionUseCase.extractFile(request, file);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
