package com.fiap.fiap_video_extractor.api.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/ping")
public class PingHandler {

    @GetMapping()
    public ResponseEntity<String> ping(
    ) {
        return new ResponseEntity<>("pong", HttpStatus.OK);
    }
}
