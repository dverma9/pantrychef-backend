package com.pantrychef.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // Connection timeout: 5 seconds
        factory.setConnectTimeout(5_000);
        // Read timeout: 30 seconds — Gemini can be slow on free tier
        factory.setReadTimeout(30_000);
        return new RestTemplate(factory);
    }
}