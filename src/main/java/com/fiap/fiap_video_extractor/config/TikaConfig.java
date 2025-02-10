package com.fiap.fiap_video_extractor.config;

import org.apache.tika.Tika;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TikaConfig {

    @Bean
    public Tika getTika() {
        return new Tika();
    }
}
