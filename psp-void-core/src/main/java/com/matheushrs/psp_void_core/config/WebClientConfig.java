package com.matheushrs.psp_void_core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${services.postgres-db.url}")
    private String postgresDbUrl;

    @Value("${services.elasticsearch-db.url}")
    private String elasticsearchDbUrl;

    @Bean("postgresDbWebClient")
    public WebClient postgresDbWebClient() {
        return WebClient.builder().baseUrl(postgresDbUrl).build();
    }

    @Bean("elasticsearchDbWebClient")
    public WebClient elasticsearchDbWebClient() {
        return WebClient.builder().baseUrl(elasticsearchDbUrl).build();
    }
}
