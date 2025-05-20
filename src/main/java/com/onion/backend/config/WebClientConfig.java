package com.onion.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient esWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:9200")
                 .defaultHeaders(h -> h.setBasicAuth("elastic","onion1!")) // 인증이 필요하면
                .build();
    }
}

